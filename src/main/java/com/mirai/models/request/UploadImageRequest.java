package com.mirai.models.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
public class UploadImageRequest {
    private MultipartFile image;
}
