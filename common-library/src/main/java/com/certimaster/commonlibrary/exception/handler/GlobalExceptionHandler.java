package com.certimaster.commonlibrary.exception.handler;

import com.certimaster.commonlibrary.exception.business.BusinessException;
import com.certimaster.commonlibrary.exception.business.ForbiddenException;
import com.certimaster.commonlibrary.exception.business.ResourceNotFoundException;
import com.certimaster.commonlibrary.exception.business.UnauthorizedException;
import com.certimaster.commonlibrary.exception.business.ValidationException;
import com.certimaster.commonlibrary.exception.communication.ServiceTimeoutException;
import com.certimaster.commonlibrary.exception.communication.ServiceUnavailableException;
import com.certimaster.commonlibrary.exception.data.DataIntegrityException;
import com.certimaster.commonlibrary.exception.data.OptimisticLockException;
import com.certimaster.commonlibrary.exception.dto.ErrorResponse;
import com.certimaster.commonlibrary.exception.others.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Global exception handler for all REST controllers
 * Catches and handles all exceptions in a centralized way
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.exception.include-stack-trace:false}")
    private boolean includeStackTrace;

    @Value("${app.exception.log-stack-trace:true}")
    private boolean logStackTrace;

    // ========================================================================
    // CUSTOM EXCEPTIONS
    // ========================================================================

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Resource not found: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        ErrorResponse response = ErrorResponse.of(ex)
                .withRequestContext(request.getRequestURI(), request.getMethod());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    /**
     * Handle BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        log.warn("Business exception: {} - Code: {}", ex.getMessage(), ex.getErrorCode());

        ErrorResponse response = ErrorResponse.of(ex)
                .withRequestContext(request.getRequestURI(), request.getMethod());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Handle ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        log.warn("Validation exception: {} - Field errors: {}",
                ex.getMessage(), ex.getFieldErrors());

        ErrorResponse response = ErrorResponse.of(ex)
                .withRequestContext(request.getRequestURI(), request.getMethod());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Handle UnauthorizedException
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request) {

        log.warn("Unauthorized access: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        ErrorResponse response = ErrorResponse.of(ex)
                .withRequestContext(request.getRequestURI(), request.getMethod());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * Handle ForbiddenException
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            ForbiddenException ex,
            HttpServletRequest request) {

        log.warn("Access forbidden: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        ErrorResponse response = ErrorResponse.of(ex)
                .withRequestContext(request.getRequestURI(), request.getMethod());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    /**
     * Handle ServiceUnavailableException
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleServiceUnavailableException(
            ServiceUnavailableException ex,
            HttpServletRequest request) {

        log.error("Service unavailable: {} - Service: {}",
                ex.getMessage(), ex.getDetail("serviceName"));

        ErrorResponse response = ErrorResponse.of(ex)
                .withRequestContext(request.getRequestURI(), request.getMethod());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    /**
     * Handle ServiceTimeoutException
     */
    @ExceptionHandler(ServiceTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleServiceTimeoutException(
            ServiceTimeoutException ex,
            HttpServletRequest request) {

        log.error("Service timeout: {} - Details: {}", ex.getMessage(), ex.getDetails());

        ErrorResponse response = ErrorResponse.of(ex)
                .withRequestContext(request.getRequestURI(), request.getMethod());

        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body(response);
    }

    /**
     * Handle RateLimitExceededException
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(
            RateLimitExceededException ex,
            HttpServletRequest request) {

        log.warn("Rate limit exceeded: {} - Details: {}", ex.getMessage(), ex.getDetails());

        ErrorResponse response = ErrorResponse.of(ex)
                .withRequestContext(request.getRequestURI(), request.getMethod());

        // Add Retry-After header if available
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS);

        if (ex.hasDetail("retryAfterSeconds")) {
            Long retryAfter = (Long) ex.getDetail("retryAfterSeconds");
            builder.header("Retry-After", String.valueOf(retryAfter));
        }

        return builder.body(response);
    }

    /**
     * Handle DataIntegrityException
     */
    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityException(
            DataIntegrityException ex,
            HttpServletRequest request) {

        log.warn("Data integrity violation: {} - Details: {}", ex.getMessage(), ex.getDetails());

        ErrorResponse response = ErrorResponse.of(ex)
                .withRequestContext(request.getRequestURI(), request.getMethod());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    /**
     * Handle OptimisticLockException
     */
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(
            OptimisticLockException ex,
            HttpServletRequest request) {

        log.warn("Optimistic lock failure: {} - Details: {}", ex.getMessage(), ex.getDetails());

        ErrorResponse response = ErrorResponse.of(ex)
                .withRequestContext(request.getRequestURI(), request.getMethod());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // ========================================================================
    // SPRING FRAMEWORK EXCEPTIONS
    // ========================================================================

    /**
     * Handle MethodArgumentNotValidException (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed for {} field(s): {}", fieldErrors.size(), fieldErrors.keySet());

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("VALIDATION_ERROR")
                .message("Validation failed for request")
                .fieldErrors(fieldErrors)
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Handle ConstraintViolationException
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        Map<String, String> fieldErrors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        log.warn("Constraint violation: {}", fieldErrors);

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("CONSTRAINT_VIOLATION")
                .message("Constraint validation failed")
                .fieldErrors(fieldErrors)
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Handle HttpMessageNotReadableException
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.warn("Malformed JSON request: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("MALFORMED_REQUEST")
                .message("Malformed JSON request")
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Handle MissingServletRequestParameterException
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        log.warn("Missing request parameter: {} ({})", ex.getParameterName(), ex.getParameterType());

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("MISSING_PARAMETER")
                .message(String.format("Required parameter '%s' is missing", ex.getParameterName()))
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build()
                .addDetail("parameterName", ex.getParameterName())
                .addDetail("parameterType", ex.getParameterType());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Handle MethodArgumentTypeMismatchException
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        log.warn("Method argument type mismatch: {} - Expected: {}, Provided: {}",
                ex.getName(), ex.getRequiredType(), ex.getValue());

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("TYPE_MISMATCH")
                .message(String.format("Invalid value for parameter '%s'", ex.getName()))
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build()
                .addDetail("parameterName", ex.getName())
                .addDetail("expectedType", ex.getRequiredType() != null ?
                        ex.getRequiredType().getSimpleName() : "unknown")
                .addDetail("providedValue", ex.getValue());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Handle NoHandlerFoundException (404)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("ENDPOINT_NOT_FOUND")
                .message(String.format("No endpoint found for %s %s",
                        ex.getHttpMethod(), ex.getRequestURL()))
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    /**
     * Handle HttpRequestMethodNotSupportedException
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        log.warn("Method not supported: {} for path {}", ex.getMethod(), request.getRequestURI());

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("METHOD_NOT_SUPPORTED")
                .message(String.format("HTTP method '%s' is not supported for this endpoint",
                        ex.getMethod()))
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build()
                .addDetail("supportedMethods", ex.getSupportedHttpMethods());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header("Allow", Objects.requireNonNull(ex.getSupportedHttpMethods()).stream()
                        .map(HttpMethod::name)
                        .collect(Collectors.joining(", ")))
                .body(response);
    }

    // ========================================================================
    // SPRING SECURITY EXCEPTIONS
    // ========================================================================

    /**
     * Handle AuthenticationException
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        log.warn("Authentication failed: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("AUTHENTICATION_FAILED")
                .message("Authentication failed: " + ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * Handle BadCredentialsException
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {

        log.warn("Bad credentials: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("INVALID_CREDENTIALS")
                .message("Invalid username or password")
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * Handle AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("ACCESS_DENIED")
                .message("Access denied to this resource")
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    // ========================================================================
    // DATABASE EXCEPTIONS
    // ========================================================================

    /**
     * Handle DataIntegrityViolationException
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.error("Data integrity violation: {}", ex.getMessage());

        String message = "Data integrity violation";
        String errorCode = "DATA_INTEGRITY_VIOLATION";

        // Try to parse specific constraint violations
        String exMessage = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";

        if (exMessage.contains("unique") || exMessage.contains("duplicate")) {
            message = "Duplicate entry - this record already exists";
            errorCode = "DUPLICATE_ENTRY";
        } else if (exMessage.contains("foreign key") || exMessage.contains("constraint")) {
            message = "Cannot perform operation - referenced by other records";
            errorCode = "CONSTRAINT_VIOLATION";
        } else if (exMessage.contains("not null") || exMessage.contains("null value")) {
            message = "Required field is missing";
            errorCode = "REQUIRED_FIELD_MISSING";
        }

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    /**
     * Handle ObjectOptimisticLockingFailureException
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
            ObjectOptimisticLockingFailureException ex,
            HttpServletRequest request) {

        log.warn("Optimistic locking failure: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("OPTIMISTIC_LOCK_FAILURE")
                .message("The record has been modified by another user. Please refresh and try again.")
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build()
                .addDetail("entityName", ex.getPersistentClassName())
                .addDetail("identifier", ex.getIdentifier());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    // ========================================================================
    // GENERIC EXCEPTION HANDLER
    // ========================================================================

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        if (logStackTrace) {
            log.error("Unexpected error occurred", ex);
        } else {
            log.error("Unexpected error: {}", ex.getMessage());
        }

        ErrorResponse response = ErrorResponse.builder()
                .success(false)
                .errorCode("INTERNAL_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .timestamp(java.time.LocalDateTime.now())
                .path(request.getRequestURI())
                .method(request.getMethod())
                .build();

        // Include stack trace in development mode
        if (includeStackTrace) {
            List<String> stackTrace = Arrays.stream(ex.getStackTrace())
                    .limit(10) // Limit to first 10 frames
                    .map(StackTraceElement::toString)
                    .collect(Collectors.toList());
            response.setStackTrace(stackTrace);
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
