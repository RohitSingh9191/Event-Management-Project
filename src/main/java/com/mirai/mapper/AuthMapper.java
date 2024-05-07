package com.mirai.mapper;

import com.mirai.data.entities.UserAuth;
import com.mirai.models.request.UserAuthRequest;
import com.mirai.models.response.AuthResponse;
import java.util.Date;
import java.util.UUID;

public class AuthMapper {

    public static UserAuth mapAuthRequestToUserAuth(UserAuthRequest user, String password) {
        return UserAuth.builder()
                .userid(UUID.randomUUID().toString())
                .name(user.getName())
                .email(user.getEmail())
                .password(password)
                .about(user.getAbout())
                .createdAt(new Date())
                .modifiedAt(new Date())
                .build();
    }

    public static AuthResponse mapAuthResponse(UserAuth userAuth) {
        return AuthResponse.builder()
                .userid(userAuth.getUserid())
                .name(userAuth.getName())
                .email(userAuth.getEmail())
                .about(userAuth.getAbout())
                .build();
    }
}
