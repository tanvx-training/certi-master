package com.certimaster.common_library.exception.communication;


import com.certimaster.common_library.exception.business.BaseException;

/**
 * Exception for service timeout scenarios
 */
public class ServiceTimeoutException extends BaseException {

    public ServiceTimeoutException(String serviceName, long timeoutMs) {
        super("SERVICE_TIMEOUT",
                String.format("Request to service '%s' timed out after %d ms", serviceName, timeoutMs));
        addDetail("serviceName", serviceName);
        addDetail("timeoutMs", timeoutMs);
    }

    public ServiceTimeoutException(String serviceName, String operation, long timeoutMs) {
        super("SERVICE_TIMEOUT",
                String.format("Operation '%s' on service '%s' timed out after %d ms",
                        operation, serviceName, timeoutMs));
        addDetail("serviceName", serviceName);
        addDetail("operation", operation);
        addDetail("timeoutMs", timeoutMs);
    }
}
