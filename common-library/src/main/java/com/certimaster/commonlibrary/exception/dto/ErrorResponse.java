package com.certimaster.commonlibrary.exception.dto;

import com.certimaster.commonlibrary.exception.business.BaseException;
import com.certimaster.commonlibrary.exception.business.ValidationException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standardized error response structure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;
    private String errorCode;
    private String message;
    private String path;
    private String method;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private Map<String, Object> details;
    private Map<String, String> fieldErrors;
    private List<String> stackTrace;

    /**
     * Create simple error response
     */
    public static ErrorResponse of(String errorCode, String message) {
        return ErrorResponse.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response from exception
     */
    public static ErrorResponse of(BaseException exception) {
        return ErrorResponse.builder()
                .success(false)
                .errorCode(exception.getErrorCode())
                .message(exception.getMessage())
                .details(exception.getDetails())
                .timestamp(exception.getTimestamp())
                .build();
    }

    /**
     * Create error response with validation errors
     */
    public static ErrorResponse of(ValidationException exception) {
        return ErrorResponse.builder()
                .success(false)
                .errorCode(exception.getErrorCode())
                .message(exception.getMessage())
                .fieldErrors(exception.getFieldErrors())
                .timestamp(exception.getTimestamp())
                .build();
    }

    /**
     * Add request context
     */
    public ErrorResponse withRequestContext(String path, String method) {
        this.path = path;
        this.method = method;
        return this;
    }

    /**
     * Add detail
     */
    public ErrorResponse addDetail(String key, Object value) {
        if (this.details == null) {
            this.details = new java.util.HashMap<>();
        }
        this.details.put(key, value);
        return this;
    }
}
