package com.certimaster.common_library.exception.communication;


import com.certimaster.common_library.exception.business.BaseException;

import java.util.Map;

/**
 * Exception for service communication failures
 */
public class ServiceUnavailableException extends BaseException {

    public ServiceUnavailableException(String serviceName) {
        super("SERVICE_UNAVAILABLE",
                String.format("Service '%s' is temporarily unavailable", serviceName));
        addDetail("serviceName", serviceName);
    }

    public ServiceUnavailableException(String serviceName, String message) {
        super("SERVICE_UNAVAILABLE", message);
        addDetail("serviceName", serviceName);
    }

    public ServiceUnavailableException(String serviceName, Throwable cause) {
        super("SERVICE_UNAVAILABLE",
                String.format("Service '%s' is temporarily unavailable", serviceName),
                cause);
        addDetail("serviceName", serviceName);
    }

    public ServiceUnavailableException(String serviceName, String message, Map<String, Object> details) {
        super("SERVICE_UNAVAILABLE", message, details);
        addDetail("serviceName", serviceName);
    }
}
