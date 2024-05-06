package com.mirai.exception.customException;

public class MeraiException extends RuntimeException {
    private final ErrorCode errorCode;

    public MeraiException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
