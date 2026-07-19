package com.fitverse.api.common.exception;

/**
 * Thrown when an operation would create a resource that already exists
 * (e.g. registering with an email that's already taken). Mapped to HTTP 409.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
