package com.mirai.mapper;

import com.mirai.constants.UserStatus;
import com.mirai.data.entities.Checkin;
import com.mirai.data.entities.Users;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.*;
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
                .field1(user.getField1())
                .field2(user.getField2())
                .field3(user.getField3())
                .field4(user.getField4())
                .date(user.getModifiedAt())
                .build();
    }

    public static UserResponse mapUserToGetAllUserResponse(Users user, String imageUrl, Boolean checkIn) {
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
                .field1(user.getField1())
                .field2(user.getField2())
                .field3(user.getField3())
                .field4(user.getField4())
                .checkIn(checkIn)
                .build();
    }

    public static CheckedInUserResponse mapUserToGetAllCheckedInUserResponse(Users user, String imageUrl) {
        return CheckedInUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .company(user.getCompany())
                .designation(user.getDesignation())
                .imageUrl(imageUrl)
                .field1(user.getField1())
                .field2(user.getField2())
                .field3(user.getField3())
                .field4(user.getField4())
                .build();
    }

    public static SpeakerResponse mapUserToGetAllSpeakerResponse(Users user, String imageUrl) {
        return SpeakerResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .company(user.getCompany())
                .designation(user.getDesignation())
                .imageUrl(imageUrl)
                .field1(user.getField1())
                .field2(user.getField2())
                .field3(user.getField3())
                .field4(user.getField4())
                .build();
    }

    public static ConfirmedUserResponse mapUserToGetAllConfirmedUserResponse(Users user, String imageUrl) {
        return ConfirmedUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .company(user.getCompany())
                .designation(user.getDesignation())
                .imageUrl(imageUrl)
                .field1(user.getField1())
                .field2(user.getField2())
                .field3(user.getField3())
                .field4(user.getField4())
                .build();
    }

    public static UserResponse mapUserToUserDesbordResponse(Users user, String imageUrl, String qrUrl) {
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
                .qrUrl(qrUrl)
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
        user.setDesignation(userRequest.getDesignation());
        user.setLinkedInProfile(userRequest.getLinkedInProfile());
        user.setType(userRequest.getType());
        user.setField1(userRequest.getField1());
        user.setField2(userRequest.getField2());
        user.setField3(userRequest.getField3());
        user.setField4(userRequest.getField4());
        user.setModifiedAt(new Date());
        return user;
    }
}
