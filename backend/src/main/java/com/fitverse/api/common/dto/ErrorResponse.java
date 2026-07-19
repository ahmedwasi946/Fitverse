package com.fitverse.api.common.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Uniform error body returned by every failed API call.
 * {@code fieldErrors} is only populated for bean-validation failures
 * (e.g. a missing/invalid field on a request DTO).
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, Map.of());
    }

    public static ErrorResponse ofValidation(int status, String error, String message, String path, Map<String, String> fieldErrors) {
        return new ErrorResponse(Instant.now(), status, error, message, path, fieldErrors);
    }
}
