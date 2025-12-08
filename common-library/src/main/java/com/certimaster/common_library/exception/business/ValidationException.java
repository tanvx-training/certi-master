package com.certimaster.common_library.exception.business;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception for validation failures
 */
public class ValidationException extends BaseException {

    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.fieldErrors = new HashMap<>();
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super("VALIDATION_ERROR", message);
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : new HashMap<>();
    }

    public ValidationException(String message, Throwable cause) {
        super("VALIDATION_ERROR", message, cause);
        this.fieldErrors = new HashMap<>();
    }

    /**
     * Add field error
     */
    public void addFieldError(String field, String error) {
        this.fieldErrors.put(field, error);
    }

    /**
     * Add multiple field errors
     */
    public ValidationException addFieldErrors(Map<String, String> errors) {
        if (errors != null) {
            this.fieldErrors.putAll(errors);
        }
        return this;
    }

    /**
     * Get all field errors
     */
    public Map<String, String> getFieldErrors() {
        return new HashMap<>(fieldErrors);
    }

    /**
     * Check if has field errors
     */
    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }

    // Factory methods

    public static ValidationException singleField(String field, String error) {
        ValidationException ex = new ValidationException("Validation failed");
        ex.addFieldError(field, error);
        return ex;
    }

    public static ValidationException multipleFields(Map<String, String> fieldErrors) {
        return new ValidationException("Validation failed for multiple fields", fieldErrors);
    }
}
