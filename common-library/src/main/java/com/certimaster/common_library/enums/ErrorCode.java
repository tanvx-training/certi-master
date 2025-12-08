package com.certimaster.common_library.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Centralized error codes for the entire system
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // General Errors (1000-1099)
    INTERNAL_ERROR("1000", "Internal server error occurred"),
    INVALID_REQUEST("1001", "Invalid request parameters"),
    VALIDATION_ERROR("1002", "Validation failed"),
    RESOURCE_NOT_FOUND("1003", "Resource not found"),
    DUPLICATE_RESOURCE("1004", "Resource already exists"),
    OPERATION_FAILED("1005", "Operation failed"),

    // Authentication & Authorization Errors (1100-1199)
    UNAUTHORIZED("1100", "Authentication required"),
    INVALID_CREDENTIALS("1101", "Invalid username or password"),
    TOKEN_EXPIRED("1102", "Token has expired"),
    TOKEN_INVALID("1103", "Invalid token"),
    FORBIDDEN("1104", "Access denied"),
    ACCOUNT_DISABLED("1105", "Account is disabled"),
    ACCOUNT_LOCKED("1106", "Account is locked"),

    // User Management Errors (1200-1299)
    USERNAME_EXISTS("1200", "Username already exists"),
    EMAIL_EXISTS("1201", "Email already exists"),
    USER_NOT_FOUND("1202", "User not found"),
    INVALID_PASSWORD("1203", "Invalid password"),
    PASSWORD_MISMATCH("1204", "Passwords do not match"),

    // Exam Service Errors (1300-1399)
    CERTIFICATION_NOT_FOUND("1300", "Certification not found"),
    EXAM_FORMAT_NOT_FOUND("1301", "Exam format not found"),
    TOPIC_NOT_FOUND("1302", "Topic not found"),
    QUESTION_NOT_FOUND("1303", "Question not found"),
    INSUFFICIENT_QUESTIONS("1304", "Not enough questions available"),
    EXAM_GENERATION_FAILED("1305", "Failed to generate exam"),

    // Result Service Errors (1400-1499)
    ATTEMPT_NOT_FOUND("1400", "Exam attempt not found"),
    ATTEMPT_ALREADY_COMPLETED("1401", "Exam attempt already completed"),
    INVALID_ANSWER_FORMAT("1402", "Invalid answer format"),
    SCORING_FAILED("1403", "Failed to calculate score"),

    // Blog Service Errors (1500-1599)
    POST_NOT_FOUND("1500", "Blog post not found"),
    CATEGORY_NOT_FOUND("1501", "Category not found"),
    SLUG_EXISTS("1502", "Slug already exists"),

    // Rate Limiting Errors (1600-1699)
    RATE_LIMIT_EXCEEDED("1600", "Rate limit exceeded"),

    // Service Communication Errors (1700-1799)
    SERVICE_UNAVAILABLE("1700", "Service temporarily unavailable"),
    SERVICE_TIMEOUT("1701", "Service request timeout"),
    CIRCUIT_BREAKER_OPEN("1702", "Service circuit breaker is open");

    private final String code;
    private final String message;

    /**
     * Get ErrorCode by code string
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }
}
