package com.mirai.service.compareFaces;

import org.springframework.web.multipart.MultipartFile;

public interface CompareFacesService {
    Integer faceCompare(MultipartFile sourceImageFile, Boolean createIndexing);
}
