package com.mirai.service.user;

import com.google.zxing.WriterException;
import com.mirai.models.request.UserFilters;
import com.mirai.models.request.UserRequest;
import com.mirai.models.response.*;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse save(UserRequest userRequest);

    List<UserResponse> getAll();

    UserResponseList getAllUsers(UserFilters userFilters);

    CheckedInUserResponseList getAllCheckInUsers();

    String updateUserStatus(Integer id, String status) throws WriterException, IOException;

    UserResponse getUserProfile(Integer id);

    MessageResponse userCheckin(Integer id);

    MessageResponse userCheckout(Integer id);

    UploadImageResponse uploadPhoto(Integer id, MultipartFile image) throws IOException;

    MessageResponse checkInByImage(MultipartFile image, Boolean createIndexing);

    UserResponse updateUser(Integer id, UserRequest userRequest);

    String resendConfirmationMsg(Integer id) throws IOException, WriterException;

    String resendConfirmationMsgToAll() throws IOException, WriterException;

    SpeakerResponseList getConfirmedSpeaker();

    ConfirmedUserResponseList getAllcomfirmedUser();
}
