package com.chensoul.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
