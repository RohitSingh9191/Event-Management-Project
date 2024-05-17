package com.mirai.service.compareFaces.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.mirai.service.compareFaces.CompareFacesToAll;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CompareFaceToAllImpl implements CompareFacesToAll {

    private static final String SOURCE_IMAGE_BUCKET = "your-source-bucket-name";
    private static final String SOURCE_IMAGE_KEY = "source-image-key.jpg";
    private static final String TARGET_IMAGE_BUCKET = "your-target-bucket-name";

    private AmazonRekognition rekognitionClient;
    private AmazonS3 s3Client;

    // Compare faces using a user-provided image as the source
    public void faceCompare(ByteBuffer sourceImageBytes) {
        if (sourceImageBytes == null) {
            System.out.println("No source image provided");
            return;
        }
        compare(sourceImageBytes);
    }

    private void compare(ByteBuffer sourceImageBytes) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("YOUR_ACCESS_KEY", "YOUR_SECRET_KEY");

        Regions regions = Regions.AP_SOUTH_1;
        rekognitionClient = AmazonRekognitionClientBuilder.standard()
                .withRegion(regions)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(regions)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
        Float similarityThreshold = 70F;
        Image source = new Image().withBytes(sourceImageBytes);
        List<String> targetImageKeys = s3Client.listObjects(TARGET_IMAGE_BUCKET).getObjectSummaries().stream()
                .map(s -> s.getKey())
                .collect(Collectors.toList());

        for (String targetImageKey : targetImageKeys) {
            ByteBuffer targetImageBytes = getImageBytesFromS3(TARGET_IMAGE_BUCKET, targetImageKey);
            if (targetImageBytes == null) {
                System.out.println("Failed to load target image: " + targetImageKey);
                continue;
            }

            Image target = new Image().withBytes(targetImageBytes);
            CompareFacesRequest request = new CompareFacesRequest()
                    .withSourceImage(source)
                    .withTargetImage(target)
                    .withSimilarityThreshold(similarityThreshold);

            CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);
            displayResults(compareFacesResult, targetImageKey);
        }
    }

    private ByteBuffer getImageBytesFromS3(String bucket, String key) {
        try (S3Object s3Object = s3Client.getObject(bucket, key);
                InputStream objectData = s3Object.getObjectContent()) {
            return ByteBuffer.wrap(IOUtils.toByteArray(objectData));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayResults(CompareFacesResult compareFacesResult, String targetImageKey) {
        List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        for (CompareFacesMatch match : faceDetails) {
            ComparedFace face = match.getFace();
            BoundingBox position = face.getBoundingBox();
            System.out.println("Face in target image " + targetImageKey + " at "
                    + position.getLeft().toString() + " " + position.getTop()
                    + " matches with " + match.getSimilarity().toString()
                    + "% confidence.");
        }
        List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();
        System.out.println("There was " + uncompared.size() + " face(s) that did not match in image " + targetImageKey);
    }
}
