package com.mirai.exception.customException;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    int getErrorId();

    String getErrorCode();

    String getErrorMessage();

    HttpStatus getHttpStatus();

    void setErrorMessage(String errorMessage);
}
