package com.mirai.service.compareFaces.impl.newFaceRec;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RekognitionCollectionService {

    private final Environment env;

    public void createCollection() {
        String accessKey = env.getProperty("accessKey");
        String secretKey = env.getProperty("secretKey");
        String collectionId = env.getProperty("aws.rekognition.collectionId");

        if (accessKey == null || secretKey == null || collectionId == null) {
            log.error("AWS credentials or collection ID are not set in the environment.");
            return;
        }

        try {
            AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTH_1)
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                    .build();

            // Check if the collection exists
            if (!doesCollectionExist(rekognitionClient, collectionId)) {
                CreateCollectionRequest request = new CreateCollectionRequest().withCollectionId(collectionId);

                CreateCollectionResult createCollectionResult = rekognitionClient.createCollection(request);
                log.info("CollectionArn: {}", createCollectionResult.getCollectionArn());
                log.info(
                        "Status code: {}",
                        createCollectionResult.getStatusCode().toString());
            } else {
                log.info("Collection {} already exists.", collectionId);
            }
            indexFacesFromS3();
        } catch (Exception e) {
            log.error("Error creating collection: {}", e.getMessage(), e);
        }
    }

    private boolean doesCollectionExist(AmazonRekognition rekognitionClient, String collectionId) {
        try {
            ListCollectionsRequest listCollectionsRequest = new ListCollectionsRequest();
            ListCollectionsResult result;
            do {
                result = rekognitionClient.listCollections(listCollectionsRequest);
                if (result.getCollectionIds().contains(collectionId)) {
                    return true;
                }
                listCollectionsRequest.setNextToken(result.getNextToken());
            } while (result.getNextToken() != null);

            return false;
        } catch (Exception e) {
            log.error("Error checking if collection exists: {}", e.getMessage(), e);
            return false;
        }
    }

    public void indexFacesFromS3() {
        String bucketName = env.getProperty("s3bucketName");
        String collectionId = env.getProperty("aws.rekognition.collectionId");
        String accessKey = env.getProperty("accessKey");
        String secretKey = env.getProperty("secretKey");
        if (bucketName == null || collectionId == null) {
            log.error("S3 bucket name or collection ID is not set in the environment.");
            return;
        }

        try {
            ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucketName);
            ListObjectsV2Result result;

            do {
                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withRegion(Regions.AP_SOUTH_1)
                        .withCredentials(
                                new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                        .build();
                result = s3Client.listObjectsV2(listObjectsRequest);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    String key = objectSummary.getKey();

                    if (!key.endsWith(".jpg") && !key.endsWith(".jpeg") && !key.endsWith(".png")) {
                        log.info("Skipping non-image file: {}", key);
                        continue;
                    }

                    Image image = new Image()
                            .withS3Object(new S3Object().withBucket(bucketName).withName(key));
                    String externalImageId = key.substring(key.lastIndexOf('/') + 1);
                    IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
                            .withCollectionId(collectionId)
                            .withImage(image)
                            .withExternalImageId(externalImageId) // Using image key as external ID
                            .withDetectionAttributes("DEFAULT");

                    AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
                            .withRegion(Regions.AP_SOUTH_1)
                            .withCredentials(
                                    new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                            .build();
                    IndexFacesResult indexFacesResult = rekognitionClient.indexFaces(indexFacesRequest);

                    List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
                    if (faceRecords != null && !faceRecords.isEmpty()) {
                        log.info("Indexed {} face(s) from image: {}", faceRecords.size(), key);
                    } else {
                        log.warn("No faces found in the image: {}", key);
                    }
                }

                listObjectsRequest.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated());

        } catch (Exception e) {
            log.error("Error indexing faces from S3: {}", e.getMessage(), e);
        }
    }
}
