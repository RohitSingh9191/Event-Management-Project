package com.mirai.exception.customException;

public class MiraiException extends RuntimeException {
    private final transient ErrorCode errorCode;

    public MiraiException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }
}
