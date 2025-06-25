package com.xszx.common.exceptions;

import com.xszx.common.errorcode.IErrorCode;

import java.util.Optional;

public class AbstractException extends RuntimeException {
    public final String errorCode;
    public final String errorMessage;

    public AbstractException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable);
        this.errorCode = errorCode.code();
        this.errorMessage = Optional.ofNullable(message.isEmpty() ? message : null).orElse(errorCode.message());
    }
}
