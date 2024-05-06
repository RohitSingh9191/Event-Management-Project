package com.mirai.service.user.impl;

import com.mirai.constants.PolicyEnum;
import com.mirai.constants.RoleEnum;
import com.mirai.data.entities.Users;
import com.mirai.data.repos.UserRepository;
import com.mirai.exception.customException.ApplicationErrorCode;
import com.mirai.exception.customException.MeraiException;
import com.mirai.mapper.UsersMapper;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.UserResponse;
import com.mirai.service.email.EmailService;
import com.mirai.service.user.UserService;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final Environment env;

    /**
     * Saves user details.
     *
     * @param userRequest The request body containing user details.
     * @return UserResponse containing the response body with saved user details.
     * @throws MeraiException if email already exists or role type is invalid.
     */
    @Override
    public UserResponse save(UserRequest userRequest) {
        String userEmail = userRequest.getEmail();
        if (userRepository.findByEmail(userEmail) != null) {
            log.error("Email {} already exists.", userEmail);
            throw new MeraiException(ApplicationErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (!isEnumValue(RoleEnum.class, userRequest.getType())) {
            log.error("Invalid role type: {}", userRequest.getType());
            throw new MeraiException(ApplicationErrorCode.INVALID_ROLE_TYPE);
        }
        Boolean policy = convertPolicyStringToBoolean(userRequest.getIsPolicyAcepted());
        Users user = UsersMapper.mapUserRequestToUser(userRequest, policy);
        mailService(user);
        userRepository.save(user);
        log.info("User saved successfully: {}", user);
        return UsersMapper.mapUserToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAll() {
        List<Users> userList = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (Users user : userList) {
            userResponseList.add(UsersMapper.mapUserToUserResponse(user));
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

    private static Boolean convertPolicyStringToBoolean(String policyString) {
        if (PolicyEnum.ACCEPT.name().equalsIgnoreCase(policyString)) {
            return true;
        } else if (PolicyEnum.REJECT.name().equalsIgnoreCase(policyString)) {
            return false;
        } else {
            throw new MeraiException(ApplicationErrorCode.INVALID_POLICY_TYPE);
        }
    }
}
