package com.mirai.exception.customException;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ApplicationErrorCode implements ErrorCode {
    EMAIL_ALREADY_EXISTS(100001, "EMAIL_ALREADY_EXISTS", "Email id already exists", HttpStatus.CONFLICT),
    USERNAME_NOT_VALID(100002, "USERNAME_NOT_VALID", "username not valid", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(100002, "INVALID_EMAIL", "Invalid Email", HttpStatus.BAD_REQUEST),
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
