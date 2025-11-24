package com.certimaster.commonlibrary.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * Secret key for signing JWT tokens
     * Must be at least 256 bits (32 characters) for HS256
     * Recommended 512 bits (64 characters) for HS512
     */
    private String secret = "your-256-bit-secret-key-change-this-in-production-for-security-purposes";

    /**
     * JWT token expiration time in milliseconds
     * Default: 1 hour (3600000 ms)
     */
    private Long expiration = 3600000L;

    /**
     * Refresh token expiration time in milliseconds
     * Default: 7 days (604800000 ms)
     */
    private Long refreshExpiration = 604800000L;

    /**
     * Token header name
     */
    private String header = "Authorization";

    /**
     * Token prefix (e.g., "Bearer ")
     */
    private String prefix = "Bearer ";

    /**
     * Issuer name
     */
    private String issuer = "certimaster";
}
