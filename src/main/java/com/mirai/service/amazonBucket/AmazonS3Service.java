package com.mirai.service.amazonBucket;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.mirai.models.response.UploadImageResponse;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface AmazonS3Service {

    UploadImageResponse uploadPhoto(MultipartFile image, String fileName)
            throws IOException, AmazonServiceException, SdkClientException;

    String publicLinkOfImage(String fileName, String bucketName);

    String uploadQRCodeToS3(byte[] qrCodeImage, String id)
            throws IOException, AmazonServiceException, SdkClientException;
}
