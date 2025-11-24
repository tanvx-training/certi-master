package com.certimaster.commonlibrary.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppConstants {

    // Date/Time Patterns
    public static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_PATTERN = "dd-MM-yyyy";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String TIMESTAMP_PATTERN = "dd-MM-yyyy'T'HH:mm:ss.SSS";

    // Pagination Defaults
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_DIRECTION = "DESC";

    // Cache TTL (in seconds)
    public static final long CACHE_TTL_SHORT = 300;      // 5 minutes
    public static final long CACHE_TTL_MEDIUM = 1800;    // 30 minutes
    public static final long CACHE_TTL_LONG = 3600;      // 1 hour
    public static final long CACHE_TTL_VERY_LONG = 86400; // 24 hours

    // HTTP Headers
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USERNAME = "X-Username";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_CORRELATION_ID = "X-Correlation-Id";

    // Regex Patterns
    public static final String REGEX_USERNAME = "^[a-zA-Z0-9_-]{3,50}$";
    public static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String REGEX_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String REGEX_PHONE = "^\\+?[1-9]\\d{1,14}$";

    // System User
    public static final String SYSTEM_USER = "SYSTEM";
    public static final String ANONYMOUS_USER = "ANONYMOUS";

    // File Upload
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif"};
    public static final String[] ALLOWED_DOCUMENT_TYPES = {"application/pdf", "application/msword"};
}
