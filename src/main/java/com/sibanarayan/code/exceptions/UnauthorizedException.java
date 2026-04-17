package com.sibanarayan.code.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String msg) {
        super(msg);
    }

    public UnauthorizedException(String msg, Throwable cause) {
        super(msg, cause);
    }
}