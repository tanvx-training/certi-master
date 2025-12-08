package com.certimaster.common_library.exception.communication;

import com.certimaster.common_library.exception.business.BaseException;

/**
 * Exception thrown when circuit breaker is open
 */
public class CircuitBreakerOpenException extends BaseException {

    public CircuitBreakerOpenException(String serviceName) {
        super("CIRCUIT_BREAKER_OPEN",
                String.format("Circuit breaker is open for service '%s'. Please try again later.",
                        serviceName));
        addDetail("serviceName", serviceName);
    }

    public CircuitBreakerOpenException(String serviceName, String details) {
        super("CIRCUIT_BREAKER_OPEN",
                String.format("Circuit breaker is open for service '%s': %s", serviceName, details));
        addDetail("serviceName", serviceName);
        addDetail("details", details);
    }
}
