package com.mirai.service.compareFaces.impl.newFaceRec;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.mirai.service.compareFaces.CompareFacesService;
import java.nio.ByteBuffer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Slf4j
public class CompareFaceService implements CompareFacesService {

    private final Environment env;
    private final RekognitionCollectionService rekognitionCollectionService;

    public Integer faceCompare(MultipartFile sourceImageFile) {

        //  rekognitionCollectionService.createCollection();

        String accessKey = env.getProperty("accessKey");
        String secretKey = env.getProperty("secretKey");
        String collectionId = env.getProperty("aws.rekognition.collectionId");

        if (accessKey == null || secretKey == null || collectionId == null) {
            log.error("AWS credentials or collection ID are not set in the environment.");
            return null;
        }

        if (sourceImageFile == null || sourceImageFile.isEmpty()) {
            log.error("No source image provided");
            return null;
        }

        try {
            ByteBuffer sourceImageBytes = ByteBuffer.wrap(sourceImageFile.getBytes());

            AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTH_1)
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                    .build();

            Image sourceImage = new Image().withBytes(sourceImageBytes);
            SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
                    .withCollectionId(collectionId)
                    .withImage(sourceImage)
                    .withFaceMatchThreshold(80F)
                    .withMaxFaces(1);

            SearchFacesByImageResult searchFacesByImageResult =
                    rekognitionClient.searchFacesByImage(searchFacesByImageRequest);

            if (!searchFacesByImageResult.getFaceMatches().isEmpty()) {
                FaceMatch faceMatch = searchFacesByImageResult.getFaceMatches().get(0);
                String externalImageId = faceMatch.getFace().getExternalImageId();
                log.info("Face matched with similarity: {}%", faceMatch.getSimilarity());
                log.info("Original Image Name (External Image ID): {}", externalImageId);
                String imageName = externalImageId.substring(
                        externalImageId.lastIndexOf('/') + 1, externalImageId.lastIndexOf('.'));
                int imageNumber = Integer.parseInt(imageName);
                return imageNumber;

            } else {
                log.info("No matching face found.");
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to process the source image", e);
            return null;
        }
    }
}
