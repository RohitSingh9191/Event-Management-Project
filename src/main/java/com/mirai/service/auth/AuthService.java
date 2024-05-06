package com.mirai.service.auth;

import com.mirai.models.request.JWTRequest;
import com.mirai.models.response.JWTResponse;

public interface AuthService {
    JWTResponse adminlogin(JWTRequest request);
}
