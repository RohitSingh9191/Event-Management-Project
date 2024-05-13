package com.mirai.service.user;

import com.google.zxing.WriterException;
import com.mirai.models.request.UserFilters;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.UploadImageResponse;
import com.mirai.models.response.UserResponse;
import com.mirai.models.response.UserResponseList;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse save(UserRequest userRequest);

    List<UserResponse> getAll();

    UserResponseList getAllUsers(UserFilters userFilters);

    String updateUserStatus(Integer id, String status) throws WriterException, IOException;

    UserResponse getUserProfile(Integer id);

    UserResponse userCheckin(Integer id);

    UploadImageResponse uploadPhoto(Integer id, MultipartFile image) throws IOException;
}
