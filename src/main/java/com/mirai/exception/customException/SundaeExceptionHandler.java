package com.mirai.exception.customException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SundaeExceptionHandler {
    @ExceptionHandler(SundaeException.class)
    public ResponseEntity<ErrorResponse> handlingError(SundaeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, ex.getErrorCode().getHttpStatus());
    }
}
