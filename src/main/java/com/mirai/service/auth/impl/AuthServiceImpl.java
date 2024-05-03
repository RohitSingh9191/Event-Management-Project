package com.mirai.service.auth.impl;

import com.mirai.data.entities.Users;
import com.mirai.data.repos.UserRepository;
import com.mirai.models.request.LoginRequest;
import com.mirai.models.response.LoginResponse;
import com.mirai.service.auth.AuthService;
import com.mirai.utils.EncryptionUtils;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {


}
