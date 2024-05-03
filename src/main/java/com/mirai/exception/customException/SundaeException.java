package com.mirai.exception.customException;

public class SundaeException extends RuntimeException {
    private final ErrorCode errorCode;

    public SundaeException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
