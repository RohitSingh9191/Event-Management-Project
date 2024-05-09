package com.mirai.service.user.impl;

import com.google.zxing.WriterException;
import com.mirai.constants.ConfirmationStatus;
import com.mirai.constants.PolicyEnum;
import com.mirai.constants.RoleEnum;
import com.mirai.data.entities.Users;
import com.mirai.data.repos.UserRepository;
import com.mirai.data.specifications.UserSpecifications;
import com.mirai.exception.customException.ApplicationErrorCode;
import com.mirai.exception.customException.MiraiException;
import com.mirai.mapper.UsersMapper;
import com.mirai.models.request.UserFilters;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.UserResponse;
import com.mirai.models.response.UserResponseList;
import com.mirai.service.email.EmailService;
import com.mirai.service.user.UserService;
import com.mirai.utils.MiraiUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
     * @throws MiraiException if email already exists or role type is invalid.
     */
    @Override
    public UserResponse save(UserRequest userRequest) {
        String userEmail = userRequest.getEmail();
        Users users = userRepository.findByEmail(userEmail);
        if (users != null) {
            mailService(users);
            mailSendToAdmin(users);
            users.setModifiedAt(new Date());
            userRepository.save(users);
            log.error("User creation failed: Email '{}' already exists.", userRequest.getEmail());
            throw new MiraiException(ApplicationErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (!isEnumValue(RoleEnum.class, userRequest.getType())) {
            log.error("Invalid role type: {}", userRequest.getType());
            throw new MiraiException(ApplicationErrorCode.INVALID_ROLE_TYPE);
        }
        Boolean policy = convertPolicyStringToBoolean(userRequest.getIsPolicyAcepted());
        Users user = UsersMapper.mapUserRequestToUser(userRequest, policy);
        mailService(user);
        mailSendToAdmin(users);
        userRepository.save(user);
        log.info("User saved successfully: {}", user);
        return UsersMapper.mapUserToUserResponse(user);
    }

    /**
     * Retrieves a list of users based on the provided filters.
     *
     * @param userFilters The filters to apply while fetching users.
     * @return UserResponseList containing the list of users.
     */
    @Override
    public UserResponseList getAllUsers(UserFilters userFilters) {
        log.info("Fetching users based on filters: {}", userFilters);
        checkValidationOfGetAllUsers(userFilters);
        Pageable pageable = MiraiUtils.createPageable(userFilters.getLimit(), userFilters.getOffset());
        Specification<Users> spec = UserSpecifications.searchUsers(userFilters);
        Page<Users> usersPage = userRepository.findAll(spec, pageable);
        log.info("Retrieved {} users based on filters: {}", usersPage.getNumberOfElements(), userFilters);
        return setAllUserToUserListResponse(usersPage);
    }

    /**
     * Maps a Page of Users to a UserResponseList.
     *
     * @param usersPage The Page of Users to map.
     * @return UserResponseList containing the mapped UserResponse objects.
     */
    private UserResponseList setAllUserToUserListResponse(Page<Users> usersPage) {
        log.info("Mapping users to user response list");
        int totalCount = (int) usersPage.getTotalElements();
        List<Users> usersList = usersPage.getContent();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (Users user : usersList) {
            UserResponse userResponse = UsersMapper.mapUserToUserResponse(user);
            userResponseList.add(userResponse);
        }
        log.info("Mapped {} users to user response list", usersList.size());
        return UserResponseList.builder()
                .totalCount(totalCount)
                .userResponses(userResponseList)
                .build();
    }

    /**
     * Checks the validation of the provided user filters.
     *
     * @param userFilters The UserFilters to validate.
     * @throws MiraiException if the provided filters contain invalid values.
     */
    private void checkValidationOfGetAllUsers(UserFilters userFilters) {
        log.info("Validating user filters: {}", userFilters);
        String type = userFilters.getType();
        if (type == null || type.isEmpty()) {
        } else {
            if (!Arrays.stream(RoleEnum.values())
                    .anyMatch(enumConstant -> enumConstant.name().equalsIgnoreCase(type)))
                log.error("Invalid user type: {}", type);
            throw new MiraiException(ApplicationErrorCode.INVALID_ROLE_TYPE);
        }

        String policyType = userFilters.getPolicyType();
        if (policyType == null || policyType.isEmpty()) {
        } else {
            if (!Arrays.stream(PolicyEnum.values())
                    .anyMatch(enumConstant -> enumConstant.name().equalsIgnoreCase(policyType)))
                log.error("Invalid policy type: {}", policyType);
            throw new MiraiException(ApplicationErrorCode.INVALID_POLICY_TYPE);
        }
        log.info("User filters validated successfully");
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of UserResponse objects representing all users.
     */
    @Override
    public List<UserResponse> getAll() {
        log.info("Retrieving all users from the database");
        List<Users> userList = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (Users user : userList) {
            userResponseList.add(UsersMapper.mapUserToUserResponse(user));
        }
        log.info("Retrieved {} users from the database", userResponseList.size());
        return userResponseList;
    }

    /**
     * Sends a message to the user's email address.
     *
     * @param user The user to whom the message will be sent.
     */
    private void mailService(Users user) {
        log.info("Sending email to user: {}", user.getEmail());
        try {
            String toMail = user.getEmail();
            String toCC = env.getProperty("miraiEmail");
            emailService.sentMessageToEmail(user, toMail, toCC);
            log.info("Email sent successfully to user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error occurred while sending email to user: {}", user.getEmail(), e);
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the Mirai admin email address.
     *
     * @param user The user for whom the message is intended.
     */
    private void mailSendToAdmin(Users user) {
        try {
            String toMail = env.getProperty("miraiEmail");
            String toCC = env.getProperty("toCCAdmin");
            emailService.sentMessageToAdmin(user, toMail, toCC);
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
            throw new MiraiException(ApplicationErrorCode.INVALID_POLICY_TYPE);
        }
    }

    /**
     * Confirms a user by sending a confirmation email with a QR code.
     *
     * @param id The ID of the user to confirm.
     * @return A message indicating that the email has been sent successfully.
     * @throws WriterException If an error occurs while generating the QR code.
     * @throws IOException     If an I/O error occurs while sending the email.
     */
    @Override
    public String confirmUser(Integer id) throws WriterException, IOException {
        log.info("Confirming user with ID: {}", id);
        Users user =
                userRepository.findById(id).orElseThrow(() -> new MiraiException(ApplicationErrorCode.USER_NOT_EXIST));
        user.setStatus(ConfirmationStatus.CONFIRMED.name());
        user.setModifiedAt(new Date());
        String link = "https://api.mirai.events/mirai/v1/user/" + id;
        byte[] qrCodeImage = MiraiUtils.generateQRCodeImage(link, 300, 300);
        emailService.sendEmailWithQRCode(
                user, "Confirmation Email", "Hi, Please find the QR code attached.", qrCodeImage);
        userRepository.save(user);
        log.info("Confirmation email sent successfully to {}", user.getEmail());
        return "Email send successfully at " + user.getEmail();
    }

    /**
     * Retrieves the profile of a user by their ID.
     *
     * @param id The ID of the user whose profile is to be retrieved.
     * @return The UserResponse object containing the user profile.
     * @throws MiraiException if the user with the given ID is not found.
     */
    @Override
    public UserResponse getUserProfile(Integer id) {
        log.info("Fetching profile for user with ID: {}", id);
        Users user =
                userRepository.findById(id).orElseThrow(() -> new MiraiException(ApplicationErrorCode.USER_NOT_EXIST));
        UserResponse userResponse = UsersMapper.mapUserToUserResponse(user);
        log.info("Profile fetched successfully for user with ID: {}", id);
        return userResponse;
    }
}
