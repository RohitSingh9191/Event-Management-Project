package com.mirai.service.auth;

import com.mirai.models.request.JWTRequest;
import com.mirai.models.request.UserAuthRequest;
import com.mirai.models.response.AuthResponse;
import com.mirai.models.response.JWTResponse;

public interface AuthService {
    JWTResponse adminlogin(JWTRequest request);

    AuthResponse createUser(UserAuthRequest user);
}
