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

    private final UserRepository userRepository;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        Users users = userRepository.findByEmail(loginRequest.getUsername());
        if (users == null) {
            throw new RuntimeException("User name not found");
        }

        LoginResponse loginResponse = null;
        if (!users.getEmail().isEmpty()) {

            String password = hashedPassword(loginRequest.getPassword(), users.getPasswordSalt());
            if (password.equals(users.getPasswordHash())) {

                //                loginResponse = handleLoginToken(userDetails.getId(), userDetails.getEmail());
            } else {
                throw new RuntimeException("Password not valid");
            }
        }

        return null;
    }

    private String hashedPassword(String password, String passwordSalt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return EncryptionUtils.generateHash(password, passwordSalt);
    }
}
