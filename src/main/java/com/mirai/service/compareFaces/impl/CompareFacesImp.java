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
import com.amazonaws.util.IOUtils;
import com.mirai.service.compareFaces.CompareFaces;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CompareFacesImp implements CompareFaces {

    public void faceCompare() {
        Float similarityThreshold = 70F;
        String sourceImage = "src/main/java/com/mirai/service/compareFaces/impl/source.jpeg";
        String targetImage = "src/main/java/com/mirai/service/compareFaces/impl/target.jpeg";
        ByteBuffer sourceImageBytes = null;
        ByteBuffer targetImageBytes = null;

        BasicAWSCredentials awsCreds =
                new BasicAWSCredentials("AKIAUQEK2TNL3ERF7DOT", "VaO77iBRjdVXWIvxvJYRD7+Ce2/Nvqvda4qlnevx");

        Regions regions = Regions.AP_SOUTH_1;
        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
                .withRegion(regions) // Specify the region here
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds)) // Specify credentials
                .build();

        // Load source and target images and create input parameters
        try (InputStream inputStream = new FileInputStream(new File(sourceImage))) {
            sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            System.out.println("Failed to load source image " + sourceImage);
            System.exit(1);
        }
        try (InputStream inputStream = new FileInputStream(new File(targetImage))) {
            targetImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        } catch (Exception e) {
            System.out.println("Failed to load target images: " + targetImage);
            System.exit(1);
        }

        Image source = new Image().withBytes(sourceImageBytes);
        Image target = new Image().withBytes(targetImageBytes);

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(source)
                .withTargetImage(target)
                .withSimilarityThreshold(similarityThreshold);

        // Call operation
        CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);

        // Display results
        List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
        for (CompareFacesMatch match : faceDetails) {
            ComparedFace face = match.getFace();
            BoundingBox position = face.getBoundingBox();
            System.out.println("Face at " + position.getLeft().toString()
                    + " " + position.getTop()
                    + " matches with " + match.getSimilarity().toString()
                    + "% confidence.");
        }
        List<ComparedFace> uncompared = compareFacesResult.getUnmatchedFaces();
        System.out.println("There was " + uncompared.size() + " face(s) that did not match");
    }
}
