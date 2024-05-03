package com.mirai.service.auth;

import com.mirai.models.request.LoginRequest;
import com.mirai.models.response.LoginResponse;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<LoginResponse> login(LoginRequest loginRequest)
            throws NoSuchAlgorithmException, InvalidKeySpecException;
}
