package com.mirai.service.user;

import com.mirai.models.request.UserFilters;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.UserResponse;
import com.mirai.models.response.UserResponseList;
import java.util.List;

public interface UserService {

    UserResponse save(UserRequest userRequest);

    List<UserResponse> getAll();

    UserResponseList getAllUsers(UserFilters userFilters);
}
