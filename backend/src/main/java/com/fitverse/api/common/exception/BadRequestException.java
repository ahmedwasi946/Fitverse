package com.fitverse.api.common.exception;

/**
 * Thrown for invalid requests that fail a business rule rather than bean
 * validation (e.g. checking out with an empty cart). Mapped to HTTP 400.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
