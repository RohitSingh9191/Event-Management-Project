package com.mirai.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UploadImageResponse {
    private String id;

    private String fileName;

    private String imageUrl;
}
