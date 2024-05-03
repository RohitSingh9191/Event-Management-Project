package com.mirai.service.user;

import com.mirai.models.request.UserRequest;
import com.mirai.models.response.UserResponse;
import java.util.List;

public interface UserService {

    UserResponse save(UserRequest userRequest);

    List<UserResponse> getAll();
}
