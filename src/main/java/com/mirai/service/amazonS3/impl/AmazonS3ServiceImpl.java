package com.mirai.service.amazonS3.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mirai.mapper.UsersMapper;
import com.mirai.models.response.UploadImageResponse;
import com.mirai.service.amazonS3.AmazonS3Service;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Slf4j
public class AmazonS3ServiceImpl implements AmazonS3Service {

    private Environment env;

    /**
     * Uploads an image to Amazon S3 and returns the upload response.
     *
     * @param image The image to upload.
     * @param id    The ID used in the file name.
     * @return UploadImageResponse containing information about the uploaded image.
     * @throws IOException             If an I/O exception occurs during the upload process.
     * @throws AmazonServiceException  If an error occurs in the Amazon S3 service.
     * @throws SdkClientException      If an error occurs in the AWS SDK client.
     */
    public UploadImageResponse uploadPhoto(MultipartFile image, String id)
            throws IOException, AmazonServiceException, SdkClientException {
        log.info("Starting photo upload process for image with ID: {}", id);
        String bucketName = env.getProperty("bucketName");
        Regions regions = Regions.AP_SOUTH_1;
        String accessKey = env.getProperty("accessKey");
        String secretKey = env.getProperty("secretKey");

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(regions)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentType(image.getContentType());
        metaData.setContentLength(image.getSize());

        String fileExtension = StringUtils.getFilenameExtension(Objects.requireNonNull(image.getOriginalFilename()));

        String finalFileName = id + "." + fileExtension;

        InputStream inputStream = image.getInputStream();
        PutObjectRequest request = new PutObjectRequest(bucketName, finalFileName, inputStream, metaData);
        s3Client.putObject(request);
        String url = publicLinkOfImage(finalFileName);
        UploadImageResponse uploadImageResponse = UsersMapper.mapToUploadPhotoResponse(id, url, finalFileName);
        log.info("Uploaded photo for image with ID {}: {}", id, uploadImageResponse);
        return uploadImageResponse;
    }

    /**
     * Generates a public URL for the specified file in the Amazon S3 bucket.
     *
     * @param fileName The name of the file in the bucket.
     * @return A string representing the public URL of the file.
     */
    public String publicLinkOfImage(String fileName) {
        log.info("Generating public URL for file: {}", fileName);
        String bucketName = env.getProperty("bucketName");
        String accessKey = env.getProperty("accessKey");
        String secretKey = env.getProperty("secretKey");
        Regions regions = Regions.AP_SOUTH_1;

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(regions)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, fileName).withMethod(HttpMethod.GET);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        log.info("Generated public URL for file {}: {}", fileName, url.toString());
        return url.toString();
    }
}
