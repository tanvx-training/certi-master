package com.certimaster.common_library.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT configuration properties for RS256 algorithm
 * Supports RSA asymmetric key pairs for token signing and verification
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Base64 encoded RSA private key for signing tokens
     */
    private String privateKey;

    /**
     * Base64 encoded RSA public key for verifying tokens
     */
    private String publicKey;

    /**
     * Access token expiration time in milliseconds (default: 15 minutes)
     */
    private long accessTokenExpiration = 900000L;

    /**
     * Refresh token expiration time in milliseconds (default: 7 days)
     */
    private long refreshTokenExpiration = 604800000L;

    /**
     * JWT issuer claim (default: "certimaster-auth")
     */
    private String issuer = "certimaster-auth";
}
