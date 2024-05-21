package com.mirai.service.compareFaces.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Slf4j
public class CompareFacesImp {

    private Environment env;

    public int faceCompare(MultipartFile sourceImageFile) {
        String accessKey = env.getProperty("accessKey");
        String secretKey = env.getProperty("secretKey");
        if (sourceImageFile == null || sourceImageFile.isEmpty()) {
            System.out.println("No source image provided");
            return -1; // Or any other value to indicate failure
        }
        try {
            AmazonRekognition rekognitionClient;
            AmazonS3 s3Client;
            ByteBuffer sourceImageBytes = ByteBuffer.wrap(sourceImageFile.getBytes());

            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            rekognitionClient = AmazonRekognitionClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTH_1)
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();

            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTH_1)
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                    .build();

            return compare(sourceImageBytes, s3Client, rekognitionClient);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to process the source image");
            System.out.println(e);
            return -1; // Or any other value to indicate failure
        }
    }

    private int compare(ByteBuffer sourceImageBytes, AmazonS3 s3Client, AmazonRekognition rekognitionClient) {
        Image sourceImage = new Image().withBytes(sourceImageBytes);
        String TARGET_IMAGE_BUCKET = "miraievents";
        String TARGET_IMAGE_PREFIX = "image/";

        List<String> targetImageKeys = listKeysInBucket(s3Client, TARGET_IMAGE_BUCKET, TARGET_IMAGE_PREFIX);

        for (String targetImageKey : targetImageKeys) {
            ByteBuffer targetImageBytes = getImageBytesFromS3(TARGET_IMAGE_BUCKET, targetImageKey, s3Client);
            if (targetImageBytes == null) {
                System.out.println("Failed to load target image: " + targetImageKey);
                continue;
            }

            Image targetImage = new Image().withBytes(targetImageBytes);
            CompareFacesRequest request = new CompareFacesRequest()
                    .withSourceImage(sourceImage)
                    .withTargetImage(targetImage)
                    .withSimilarityThreshold(70F);

            try {
                CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);
                System.out.println(compareFacesResult);
                int imageNumber = displayResults(compareFacesResult, targetImageKey);
                if (imageNumber != -1) {
                    return imageNumber; // Return image number if match found
                }
            } catch (InvalidParameterException e) {
                System.err.println("InvalidParameterException: " + e.getMessage());
                System.err.println("Request ID: " + e.getRequestId());
                e.printStackTrace();
                log.error("InvalidParameterException occurred for target image key: {}", targetImageKey, e);
            }
        }
        return -1; // Or any other value to indicate failure
    }

    private List<String> listKeysInBucket(AmazonS3 s3Client, String bucketName, String prefix) {
        ListObjectsRequest listObjectsRequest =
                new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix);

        ObjectListing objectListing;
        List<S3ObjectSummary> s3ObjectSummaries;
        do {
            objectListing = s3Client.listObjects(listObjectsRequest);
            s3ObjectSummaries = objectListing.getObjectSummaries();
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());

        return s3ObjectSummaries.stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }

    private ByteBuffer getImageBytesFromS3(String bucket, String key, AmazonS3 s3Client) {
        try (S3Object s3Object = s3Client.getObject(bucket, key);
                InputStream objectData = s3Object.getObjectContent()) {
            return ByteBuffer.wrap(IOUtils.toByteArray(objectData));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private int displayResults(CompareFacesResult compareFacesResult, String targetImageKey) {
        List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        if (!faceDetails.isEmpty()) {
            // Extracting the image name without extension
            String imageName =
                    targetImageKey.substring(targetImageKey.lastIndexOf('/') + 1, targetImageKey.lastIndexOf('.'));
            // Converting the image name to an integer
            int imageNumber = Integer.parseInt(imageName);

            System.out.println("Match found in image: " + imageNumber);
            for (CompareFacesMatch match : faceDetails) {
                ComparedFace face = match.getFace();
                BoundingBox position = face.getBoundingBox();
                System.out.println("Match found in target image " + imageNumber + " at "
                        + position.getLeft().toString() + " " + position.getTop()
                        + " with " + match.getSimilarity().toString()
                        + "% confidence.");
            }
            return imageNumber;
        }
        List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();
        // Extracting the image name without extension
        String imageName =
                targetImageKey.substring(targetImageKey.lastIndexOf('/') + 1, targetImageKey.lastIndexOf('.'));
        // Converting the image name to an integer
        int imageNumber = Integer.parseInt(imageName);

        System.out.println("There was " + uncompared.size() + " face(s) that did not match in image " + imageNumber);
        return -1; // Or any other value to indicate failure
    }
}
