package com.certimaster.commonexception.others;

import com.certimaster.commonexception.business.BaseException;

/**
 * Exception for rate limit violations
 */
public class RateLimitExceededException extends BaseException {

    public RateLimitExceededException(String message) {
        super("RATE_LIMIT_EXCEEDED", message);
    }

    public RateLimitExceededException(int limit, String timeWindow) {
        super("RATE_LIMIT_EXCEEDED",
                String.format("Rate limit of %d requests per %s exceeded", limit, timeWindow));
        addDetail("limit", limit);
        addDetail("timeWindow", timeWindow);
    }

    public RateLimitExceededException(int limit, String timeWindow, long retryAfterSeconds) {
        super("RATE_LIMIT_EXCEEDED",
                String.format("Rate limit exceeded. Retry after %d seconds", retryAfterSeconds));
        addDetail("limit", limit);
        addDetail("timeWindow", timeWindow);
        addDetail("retryAfterSeconds", retryAfterSeconds);
    }
}
