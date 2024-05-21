package com.mirai.service.compareFaces;

import org.springframework.web.multipart.MultipartFile;

public interface CompareFacesService {
    int faceCompare(MultipartFile sourceImageFile);
}
