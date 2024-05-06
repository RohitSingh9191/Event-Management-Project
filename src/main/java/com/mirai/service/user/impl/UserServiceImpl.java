package com.mirai.service.user.impl;

import com.mirai.constants.PolicyEnum;
import com.mirai.constants.RoleEnum;
import com.mirai.data.entities.Users;
import com.mirai.data.repos.UserRepository;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.UserResponse;
import com.mirai.service.email.EmailService;
import com.mirai.service.user.UserService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final Environment env;

    @Override
    public UserResponse save(UserRequest userRequest) {

        String userEmail = userRequest.getEmail();
        if (userRepository.findByEmail(userEmail) != null) {
            throw new RuntimeException("Email Already exits");
        }

        if (!isEnumValue(RoleEnum.class, userRequest.getType())) {
            throw new RuntimeException("Type not match");
        }

        Boolean policy = convertPolicyStringToBoolean(userRequest.getIsPolicyAcepted());

        Users user = mapUserRequestToUser(userRequest, policy);
        mailService(user);
        userRepository.save(user);

        return mapUserToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAll() {
        List<Users> userList = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (Users user : userList) {
            userResponseList.add(mapUserToUserResponse(user));
        }
        return userResponseList;
    }

    private void mailService(Users user) {
        try {
            String toMail = env.getProperty("toMail");
            String toCC = env.getProperty("toCC");

            emailService.sentMessageToEmail(user, toMail, toCC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isEnumValue(Class<? extends Enum<?>> enumClass, String value) {
        for (Enum<?> enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public Boolean convertPolicyStringToBoolean(String policyString) {
        if (PolicyEnum.ACCEPT.name().equalsIgnoreCase(policyString)) {
            return true;
        } else if (PolicyEnum.REJECT.name().equalsIgnoreCase(policyString)) {
            return false;
        } else {
            throw new RuntimeException("Policy not valid");
        }
    }

    public Users mapUserRequestToUser(UserRequest userRequest, Boolean policy) {
        return Users.builder()
                .email(userRequest.getEmail())
                .name(userRequest.getName())
                .phone(userRequest.getPhone())
                .company(userRequest.getCompany())
                .designation(userRequest.getDesignation())
                .linkedInProfile(userRequest.getLinkedInProfile())
                .type(userRequest.getType())
                .IsPolicyAccept(policy)
                .date(LocalDate.now())
                .build();
    }

    public static UserResponse mapUserToUserResponse(Users user) {
        return UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .company(user.getCompany())
                .designation(user.getDesignation())
                .phone(user.getPhone())
                .linkedInProfile(user.getLinkedInProfile())
                .type(user.getType())
                .date(user.getDate())
                .build();
    }
}
