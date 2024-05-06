package com.mirai.mapper;

import com.mirai.data.entities.Users;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.UserResponse;
import java.util.Date;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UsersMapper {

    public static UserResponse mapUserToUserResponse(Users user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .company(user.getCompany())
                .designation(user.getDesignation())
                .phone(user.getPhone())
                .linkedInProfile(user.getLinkedInProfile())
                .type(user.getType())
                .isPolicyAccept(user.getIsPolicyAccept())
                .build();
    }

    public static Users mapUserRequestToUser(UserRequest userRequest, Boolean policy) {
        return Users.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .phone(userRequest.getPhone())
                .company(userRequest.getCompany())
                .designation(userRequest.getDesignation())
                .linkedInProfile(userRequest.getLinkedInProfile())
                .type(userRequest.getType())
                .isPolicyAccept(policy)
                .modifiedAt(new Date())
                .createdAt(new Date())
                .build();
    }
}
