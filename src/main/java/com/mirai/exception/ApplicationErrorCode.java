package com.mirai.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ApplicationErrorCode implements ErrorCode {
    EMAIL_ALREADY_EXISTS(100001, "EMAIL_ALREADY_EXISTS", "Email id already exists", HttpStatus.CONFLICT),
    USERNAME_NOT_VALID(100002, "USERNAME_NOT_VALID", "username or password not valid", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_VALID(100009, "PASSWORD_NOT_VALID", "username or password not valid", HttpStatus.BAD_REQUEST),

    INVALID_EMAIL(100003, "INVALID_EMAIL", "Invalid Email", HttpStatus.BAD_REQUEST),
    INVALID_ROLE_TYPE(100004, "INVALID_ROLE_TYPE", "Role does not match", HttpStatus.BAD_REQUEST),
    INVALID_POLICY_TYPE(100005, "INVALID_POLICY_TYPE", "Policy does not match", HttpStatus.BAD_REQUEST),
    INVALID_STATUS(100004, "INVALID_STATUS", "Status does not match", HttpStatus.BAD_REQUEST),
    LIMIT_OFFSET_NOT_VALID(
            1000016,
            "LIMIT_OFFSET_NOT_VALID",
            "Limit and offset must be greater than or equal to zero",
            HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND(100007, "IMAGE_NOT_FOUND", "Image not found", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(100008, "USER_NOT_EXIST", "user not exist", HttpStatus.BAD_REQUEST),
    ;

    private final int errorId;
    private final String errorCode;
    private String errorMessage;
    private final HttpStatus httpStatus;

    @Override
    public int getErrorId() {
        return errorId;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
