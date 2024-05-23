package com.mirai.mapper;

import com.mirai.constants.UserStatus;
import com.mirai.data.entities.Checkin;
import com.mirai.data.entities.Users;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.UploadImageResponse;
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
                .status(user.getStatus())
                .date(user.getModifiedAt())
                .build();
    }

    public static UserResponse mapUserToGetAllUserResponse(Users user, String imageUrl) {
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
                .status(user.getStatus() != null ? user.getStatus() : null)
                .date(user.getModifiedAt())
                .imageUrl(imageUrl)
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
                .status(UserStatus.PENDING.name())
                .modifiedAt(new Date())
                .createdAt(new Date())
                .build();
    }

    public static Checkin mapToUserCheckin(Users user) {
        return Checkin.builder()
                .userId(user.getId())
                .checkinTime(new Date())
                .status("CHECKED_IN")
                .build();
    }

    public static UploadImageResponse mapToUploadPhotoResponse(String id, String url, String finalFileName) {
        return UploadImageResponse.builder()
                .id(id)
                .fileName(finalFileName)
                .imageUrl(url)
                .build();
    }


    public static Users mapToUpdateUser(Users user, UserRequest userRequest) {
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setCompany(userRequest.getCompany());
        user.setDesignation(user.getDesignation());
        user.setLinkedInProfile(user.getLinkedInProfile());
        user.setModifiedAt(new Date());
        return user;
    }
}
