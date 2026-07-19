package com.fitverse.api.common.exception;

/**
 * Thrown when a requested resource (user, product, order, etc.) cannot be found.
 * Handled by {@link GlobalExceptionHandler} and mapped to HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Object identifier) {
        return new ResourceNotFoundException(resource + " not found with id: " + identifier);
    }
}
