package com.mirai.service.user.impl;

import com.google.zxing.WriterException;
import com.mirai.constants.PolicyEnum;
import com.mirai.constants.RoleEnum;
import com.mirai.constants.UserStatus;
import com.mirai.data.entities.Checkin;
import com.mirai.data.entities.Users;
import com.mirai.data.repos.CheckinRepository;
import com.mirai.data.repos.UserRepository;
import com.mirai.data.specifications.UserSpecifications;
import com.mirai.exception.ApplicationErrorCode;
import com.mirai.exception.MiraiException;
import com.mirai.mapper.UsersMapper;
import com.mirai.models.request.UserFilters;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.*;
import com.mirai.service.amazonBucket.AmazonS3Service;
import com.mirai.service.compareFaces.CompareFacesService;
import com.mirai.service.email.EmailService;
import com.mirai.service.user.UserService;
import com.mirai.service.whatsApp.WhatsAppService;
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
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CheckinRepository checkinRepository;

    private final EmailService emailService;

    private final AmazonS3Service amazonS3Service;

    private final Environment env;

    private final WhatsAppService whatsAppService;

    private final CompareFacesService compareFacesService;

    /**
     * Saves user details.
     *
     * @param userRequest The request body containing user details.
     * @return UserResponse containing the response body with saved user details.
     * @throws MiraiException if email already exists or role type is invalid.
     */
    @Override
    public UserResponse save(UserRequest userRequest) {
        if (userRequest.getAdminWeb() != null && userRequest.getAdminWeb() == true) {
            return userAddByAdmin(userRequest);
        } else {
            String userEmail = userRequest.getEmail();
            Users users = userRepository.findByEmailAndStatusNot(userEmail, "INACTIVE");
            if (users != null && !users.getStatus().equalsIgnoreCase(UserStatus.INACTIVE.name())) {
                users.setModifiedAt(new Date());
                Users savedUser = userRepository.save(users);
                if (savedUser != null) {
                    whatsAppService.sendWhatsAppMessage(
                            "+91" + users.getPhone(), "registeruser", "register_user", "name", users.getName());
                    mailService(users);
                    mailSendToAdmin(users);
                }
                log.error("User creation failed: Email '{}' already exists.", userRequest.getEmail());
                throw new MiraiException(ApplicationErrorCode.EMAIL_ALREADY_EXISTS);
            }
            if (!isEnumValue(RoleEnum.class, userRequest.getType())) {
                log.error("Invalid role type: {}", userRequest.getType());
                throw new MiraiException(ApplicationErrorCode.INVALID_ROLE_TYPE);
            }
            Boolean policy = convertPolicyStringToBoolean(userRequest.getIsPolicyAcepted());
            Users user = UsersMapper.mapUserRequestToUser(userRequest, policy);
            Users savedUser = userRepository.save(user);
            if (savedUser != null) {
                mailService(user);
                mailSendToAdmin(users);
                whatsAppService.sendWhatsAppMessage(
                        "+91" + userRequest.getPhone(), "registeruser", "register_user", "name", userRequest.getName());
            }
            log.info("User saved successfully: {}", user);
            return UsersMapper.mapUserToUserResponse(user);
        }
    }

    /**
     * Manually adds a new user based on the provided user request.
     *
     * @param userRequest The user request containing user details.
     * @return UserResponse containing details of the newly added user.
     * @throws MiraiException if the email already exists in the repository.
     */
    public UserResponse userAddByAdmin(UserRequest userRequest) {
        String userEmail = userRequest.getEmail();
        log.info("Attempting to add user with email: {}", userEmail);
        Users users = userRepository.findByEmailAndStatusNot(userEmail, "INACTIVE");
        if (users != null) {
            throw new MiraiException(ApplicationErrorCode.EMAIL_ALREADY_EXISTS);
        }
        Boolean policy = convertPolicyStringToBoolean(userRequest.getIsPolicyAcepted());
        users = UsersMapper.mapUserRequestToUser(userRequest, policy);
        users = userRepository.save(users);
        log.info("UserResponse created for user with email: {}", userEmail);
        return UsersMapper.mapUserToUserResponse(users);
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
            String url = null;
            if (user.getImage() != null)
                url = amazonS3Service.publicLinkOfImage(user.getImage(), env.getProperty("bucketName"));
            Integer id = user.getId();
            Boolean checkIn = false;
            Checkin checkin = checkinRepository.getByUserId(id);
            if (checkin != null) {
                checkIn = true;
            }
            UserResponse userResponse = UsersMapper.mapUserToGetAllUserResponse(user, url, checkIn);
            userResponseList.add(userResponse);
        }
        log.info("Mapped {} users to user response list", usersList.size());
        return UserResponseList.builder()
                .totalCount(totalCount)
                .userResponses(userResponseList)
                .build();
    }

    private CheckedInUserResponseList setAllUserToCheckedInUserListResponse(List<Users> usersPage) {
        log.info("Mapping users to user response list");
        List<Users> usersList = usersPage;
        List<CheckedInUserResponse> userResponseList = new ArrayList<>();
        for (Users user : usersList) {
            String url = null;
            if (user.getImage() != null)
                url = amazonS3Service.publicLinkOfImage(user.getImage(), env.getProperty("bucketName"));
            Integer id = user.getId();
            Checkin checkin = checkinRepository.getByUserId(id);
            if (checkin != null) {
                CheckedInUserResponse userResponse = UsersMapper.mapUserToGetAllCheckedInUserResponse(user, url);
                userResponseList.add(userResponse);
            }
        }
        log.info("Mapped {} users to user response list", usersList.size());
        return CheckedInUserResponseList.builder()
                .totalCount(userResponseList.size())
                .checkedInUserResponses(userResponseList)
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
            if (Arrays.stream(RoleEnum.values())
                    .anyMatch(enumConstant -> enumConstant.name().equalsIgnoreCase(type))) {
                log.info("Type {} is valid", type);
            } else {
                throw new MiraiException(ApplicationErrorCode.INVALID_ROLE_TYPE);
            }
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
     * Retrieves a list of users based on the provided filters.
     *
     * @param userFilters The filters to apply while fetching users.
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
     * Retrieves all checked-in users based on the provided filters.
     *
     * * @return a response list containing the checked-in users
     */
    @Override
    public CheckedInUserResponseList getAllCheckInUsers() {
        List<Users> usersPage = userRepository.findAll();
        return setAllUserToCheckedInUserListResponse(usersPage);
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

    /**
     * Checks if a given string value is a valid member of the specified enum class.
     *
     * @param enumClass The enum class to check against.
     * @param value     The string value to check.
     * @return True if the value is a valid member of the enum class, false otherwise.
     */
    public boolean isEnumValue(Class<? extends Enum<?>> enumClass, String value) {
        log.info("Checking if '" + value + "' is a valid member of enum class " + enumClass.getName());
        for (Enum<?> enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equalsIgnoreCase(value)) {
                log.info("'" + value + "' is a valid member of enum class " + enumClass.getName());
                return true;
            }
        }
        log.info("'" + value + "' is not a valid member of enum class " + enumClass.getName());
        return false;
    }

    /**
     * Converts a policy string to a boolean value.
     *
     * @param policyString The policy string to convert.
     * @return True if the policy string is equivalent to ACCEPT, false if it is equivalent to REJECT.
     * @throws MiraiException If the policy string is neither ACCEPT nor REJECT.
     */
    private static Boolean convertPolicyStringToBoolean(String policyString) {
        log.info("Converting policy string '" + policyString + "' to boolean value");
        if (PolicyEnum.ACCEPT.name().equalsIgnoreCase(policyString)) {
            log.info("Policy string '" + policyString + "' is equivalent to REJECT, returning false");
            return true;
        } else if (PolicyEnum.REJECT.name().equalsIgnoreCase(policyString)) {
            log.info("Policy string '" + policyString + "' is equivalent to REJECT, returning false");
            return false;
        } else {
            log.error("Invalid policy string: '" + policyString + "'. Throwing MiraiException.");
            throw new MiraiException(ApplicationErrorCode.INVALID_POLICY_TYPE);
        }
    }

    /**
     * Updates the status of a user with the given ID.
     *
     * @param id     The ID of the user to update.
     * @param status The new status to set for the user.
     * @return A response indicating the result of the update operation.
     * @throws WriterException  If there is an error writing data.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    public String updateUserStatus(Integer id, String status) throws WriterException, IOException {
        log.info("Updating user status with ID: {}", id);
        Users user =
                userRepository.findById(id).orElseThrow(() -> new MiraiException(ApplicationErrorCode.USER_NOT_EXIST));
        checkValidationOfStatus(status);
        String resp = updateUserStatus(user, status);
        return resp;
    }

    /**
     * Updates the status of a user and performs additional actions based on the new status.
     *
     * @param user   The user whose status is being updated.
     * @param status The new status to set for the user.
     * @return A response message indicating the result of the update operation.
     * @throws IOException     If an I/O error occurs.
     * @throws WriterException If there is an error writing data.
     */
    private String updateUserStatus(Users user, String status) throws IOException, WriterException {
        Integer id = user.getId();
        String resp = null;
        if (UserStatus.REJECTED.name().equalsIgnoreCase(status)) {
            user.setStatus(UserStatus.REJECTED.name());
            whatsAppService.sendWhatsAppMessage(
                    "+91" + user.getPhone(), "rejectuser", "reject_user", "name", user.getName());
            emailService.sendRejectionEmail(user);
            resp = "Email send successfully at " + user.getEmail();
            log.info("Rejection email sent successfully to {}", user.getEmail());
        } else if (UserStatus.INACTIVE.name().equalsIgnoreCase(status)) {
            user.setStatus(UserStatus.INACTIVE.name());
            Checkin checkin = checkinRepository.getByUserId(id);
            if (checkin != null) checkinRepository.delete(checkin);
            resp = "User deleted by id " + id;
        } else if (UserStatus.PENDING.name().equalsIgnoreCase(status)) {
            user.setStatus(UserStatus.PENDING.name());
            resp = "User status update to " + "Pending with id " + id;
        } else {
            user.setStatus(UserStatus.CONFIRMED.name());
            String link = "https://api.mirai.events/miraiapp/user/" + id;
            byte[] qrCodeImage = MiraiUtils.generateQRCodeImage(link, 200, 200);
            String url = amazonS3Service.uploadQRCodeToS3(qrCodeImage, String.valueOf(id));
            whatsAppService.sendQrWhatsAppMessage("91" + user.getPhone(), user.getName(), url);
            emailService.sendEmailWithQRCode(
                    user, "Confirmation Email", "Hi, Please find the QR code attached.", qrCodeImage);
            log.info("Confirmation email sent successfully to {}", user.getEmail());
            resp = "Email send successfully at " + user.getEmail();
        }
        user.setModifiedAt(new Date());
        userRepository.save(user);
        return resp;
    }

    /**
     * Checks the validation of a user status.
     *
     * @param status The user status to validate.
     * @throws MiraiException If the user status is invalid.
     */
    private void checkValidationOfStatus(String status) {
        log.info("Checking validation of user status: {}", status);
        if (!Arrays.stream(UserStatus.values())
                .anyMatch(enumConstant -> enumConstant.name().equalsIgnoreCase(status))) {
            log.info("Invalid company type: {}", status);
            log.info("Invalid user status: {}", status);
            throw new MiraiException(ApplicationErrorCode.INVALID_STATUS);
        }
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
        if (user.getStatus() != null && user.getStatus().equalsIgnoreCase(UserStatus.INACTIVE.name()))
            throw new MiraiException(ApplicationErrorCode.USER_NOT_EXIST);

        String url = null;
        if (user.getImage() != null)
            url = amazonS3Service.publicLinkOfImage(user.getImage(), env.getProperty("bucketName"));

        String qrName = id + ".jpg";
        String qrUrl = amazonS3Service.publicLinkOfImage(qrName, env.getProperty("qrbucketName"));

        UserResponse userResponse = UsersMapper.mapUserToUserDesbordResponse(user, url, qrUrl);
        log.info("Profile fetched successfully for user with ID: {}", id);
        return userResponse;
    }

    /**
     * Checks in a user with the specified ID.
     *
     * @param id The ID of the user to check in.
     * @return UserResponse representing the checked-in user.
     * @throws MiraiException If the user with the specified ID does not exist.
     */
    @Override
    public CheckinResponse userCheckin(Integer id) {
        log.info("Checking in user with ID: {}", id);
        Users user =
                userRepository.findById(id).orElseThrow(() -> new MiraiException(ApplicationErrorCode.USER_NOT_EXIST));
        String resp = null;
        Checkin checkin = checkinRepository.getByUserId(id);
        CheckinResponse checkinResponse = new CheckinResponse();

        if (checkin != null && checkin.getStatus() != null) {
            resp = "User already checked in ";
            checkinResponse.setMessage(resp);
            return checkinResponse;
        }
        checkin = UsersMapper.mapToUserCheckin(user);
        checkinRepository.save(checkin);
        resp = "User successfully checked in ";
        log.info("User with ID {} checked in successfully " + id);
        checkinResponse.setMessage(resp);
        return checkinResponse;
    }

    /**
     * Uploads a photo for the user with the specified ID.
     *
     * @param id    The ID of the user.
     * @param image The image to upload.
     * @return UploadImageResponse containing information about the uploaded image.
     * @throws IOException If an I/O exception occurs during the upload process.
     */
    @Override
    public UploadImageResponse uploadPhoto(Integer id, MultipartFile image) throws IOException {
        log.info("Starting photo upload process for user with ID: {}", id);
        if (image == null || image.isEmpty()) throw new MiraiException(ApplicationErrorCode.IMAGE_NOT_FOUND);
        Users user =
                userRepository.findById(id).orElseThrow(() -> new MiraiException(ApplicationErrorCode.USER_NOT_EXIST));
        String fileName = String.valueOf(id);
        UploadImageResponse response = amazonS3Service.uploadPhoto(image, fileName);
        user.setImage(response.getFileName());
        userRepository.save(user);
        log.info("Uploaded photo for user with ID {}: {}", id, response);
        return response;
    }

    /**
     * Checks in a user by comparing their image.
     *
     * @param image the image file to be compared
     * @return a CheckinResponse containing the check-in status message
     */
    @Override
    public CheckinResponse checkInByImage(MultipartFile image, Boolean createIndexing) {

        Integer id = compareFacesService.faceCompare(image, createIndexing);
        Users user =
                userRepository.findById(id).orElseThrow(() -> new MiraiException(ApplicationErrorCode.USER_NOT_EXIST));
        String resp = null;
        Checkin checkin = checkinRepository.getByUserId(id);
        CheckinResponse checkinResponse = new CheckinResponse();

        if (checkin != null && checkin.getStatus() != null) {
            resp = "User " + user.getName() + " is already checked in ";
            checkinResponse.setMessage(resp);
            log.info("User with ID {} has already checked in", id);
            return checkinResponse;
        }
        checkin = UsersMapper.mapToUserCheckin(user);
        checkinRepository.save(checkin);
        resp = "User " + user.getName() + " is successfully checked in ";
        log.info("User with ID {} checked in successfully " + id);
        checkinResponse.setMessage(resp);
        return checkinResponse;
    }

    @Override
    public UserResponse updateUser(Integer id, UserRequest userRequest) {
        Users user =
                userRepository.findById(id).orElseThrow(() -> new MiraiException(ApplicationErrorCode.USER_NOT_EXIST));
        user = UsersMapper.mapToUpdateUser(user, userRequest);
        userRepository.save(user);
        return UsersMapper.mapUserToUserResponse(user);
    }

    @Override
    public String resendConfirmationMsg(Integer id) throws IOException, WriterException {
        Users user =
                userRepository.findById(id).orElseThrow(() -> new MiraiException(ApplicationErrorCode.USER_NOT_EXIST));
        user.setStatus(UserStatus.CONFIRMED.name());
        String link = "https://api.mirai.events/miraiapp/user/" + id;
        byte[] qrCodeImage = MiraiUtils.generateQRCodeImage(link, 200, 200);
        String url = amazonS3Service.uploadQRCodeToS3(qrCodeImage, String.valueOf(id));
        whatsAppService.sendReminder("91" + user.getPhone(), user.getName(), url);

        emailService.sendReminderMail(user, " Only 2 days to go!", qrCodeImage);
        log.info("Confirmation email sent successfully to {}", user.getEmail());
        String resp = "Mail send successfully at " + user.getEmail();
        return resp;
    }

    @Override
    public String resendConfirmationMsgToAll() throws IOException, WriterException {
        List<Users> user = userRepository.findByStatus("CONFIRMED");
        for (Users data : user) {
            String link = "https://api.mirai.events/miraiapp/user/" + data.getId();
            byte[] qrCodeImage = MiraiUtils.generateQRCodeImage(link, 200, 200);
            String url = amazonS3Service.uploadQRCodeToS3(qrCodeImage, String.valueOf(data.getId()));
            whatsAppService.sendReminder("91" + data.getPhone(), data.getName(), url);
            emailService.sendReminderMail(data, " Only 2 days to go!", qrCodeImage);
        }
        return "Mail successfully send to all";
    }
}
