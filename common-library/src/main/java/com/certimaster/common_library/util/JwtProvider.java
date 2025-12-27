package com.certimaster.common_library.util;

import com.certimaster.common_library.dto.JwtProperties;
import com.certimaster.common_library.exception.jwt.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * JWT Provider for token generation and validation using RS256 algorithm
 * Handles access token and refresh token generation and validation
 * 
 * Requirements:
 * - 1.1: Support RS256 algorithm with RSA key pairs
 * - 1.2: Generate temporary key pairs for development mode
 * - 1.3: Load private and public keys from Base64 encoded strings
 * - 1.4: Throw RuntimeException with descriptive error message on key loading failure
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    
    private PrivateKey privateKey;
    private PublicKey publicKey;

    /**
     * Initialize JWT Provider with RSA keys
     * Loads keys from configuration or generates temporary keys for development
     */
    @PostConstruct
    public void init() {
        try {
            if (jwtProperties.getPrivateKey() != null && jwtProperties.getPublicKey() != null
                    && !jwtProperties.getPrivateKey().isEmpty() && !jwtProperties.getPublicKey().isEmpty()) {
                // Load keys from configuration
                this.privateKey = loadPrivateKey(jwtProperties.getPrivateKey());
                this.publicKey = loadPublicKey(jwtProperties.getPublicKey());
                log.info("JWT Provider initialized with configured RSA keys using RS256 algorithm");
            } else {
                // Generate new key pair for development/testing
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                this.privateKey = keyPair.getPrivate();
                this.publicKey = keyPair.getPublic();
                log.warn("JWT Provider initialized with generated RSA keys (development mode). " +
                        "Configure jwt.private-key and jwt.public-key for production.");
            }
        } catch (Exception e) {
            log.error("Failed to initialize JWT Provider", e);
            throw new RuntimeException("Failed to initialize JWT Provider: " + e.getMessage(), e);
        }
    }


    /**
     * Load private key from Base64 encoded string
     * Supports both raw Base64 and PEM format
     * 
     * @param base64Key Base64 encoded private key
     * @return PrivateKey object
     * @throws Exception if key loading fails
     */
    private PrivateKey loadPrivateKey(String base64Key) throws Exception {
        String cleanKey = base64Key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    /**
     * Load public key from Base64 encoded string
     * Supports both raw Base64 and PEM format
     * 
     * @param base64Key Base64 encoded public key
     * @return PublicKey object
     * @throws Exception if key loading fails
     */
    private PublicKey loadPublicKey(String base64Key) throws Exception {
        String cleanKey = base64Key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    /**
     * Generate access token with user claims
     * Includes: user ID, username, email, roles, and permissions
     * 
     * Requirements:
     * - 3.1: Include user ID as subject claim
     * - 3.2: Include username, email, roles, and permissions as custom claims
     * - 3.3: Set token type claim to "access"
     * - 3.4: Set issuer, issuedAt, and expiration claims
     * - 3.5: Sign the token using RS256 algorithm with private key
     * 
     * @param userId user ID
     * @param username username
     * @param email user email
     * @param roles set of role codes
     * @param permissions set of permissions
     * @return JWT access token string
     */
    public String generateAccessToken(Long userId, String username, String email, 
                                       Set<String> roles, Set<String> permissions) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("email", email)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .claim("type", "access")
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }


    /**
     * Generate refresh token for the user
     * Contains minimal claims for security
     * 
     * Requirements:
     * - 4.1: Include user ID as subject claim
     * - 4.2: Include username and unique JTI (JWT ID) as claims
     * - 4.3: Set token type claim to "refresh"
     * - 4.4: Use configured refresh token expiration time
     * 
     * @param userId user ID
     * @param username username
     * @return JWT refresh token string
     */
    public String generateRefreshToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpiration());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", "refresh")
                .claim("jti", UUID.randomUUID().toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    /**
     * Validate JWT token
     * Checks: signature, expiration, and issuer
     * 
     * Requirements:
     * - 5.1: Verify signature using public key
     * - 5.2: Verify issuer matches configured issuer
     * - 5.3: Verify token is not expired
     * - 5.4: Throw InvalidTokenException with code "TOKEN_INVALID_SIGNATURE" for invalid signature
     * - 5.5: Throw InvalidTokenException with code "TOKEN_MALFORMED" for malformed format
     * - 5.6: Throw InvalidTokenException with code "TOKEN_EXPIRED" for expiration
     * 
     * @param token the JWT token to validate
     * @return Claims if valid
     * @throws InvalidTokenException if validation fails
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .requireIssuer(jwtProperties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_INVALID_SIGNATURE", "Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_MALFORMED", "Invalid JWT token format");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_EXPIRED", "JWT token has expired");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_UNSUPPORTED", "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_EMPTY", "JWT claims string is empty");
        } catch (Exception ex) {
            log.error("JWT validation failed: {}", ex.getMessage());
            throw new InvalidTokenException("TOKEN_INVALID", "JWT validation failed: " + ex.getMessage());
        }
    }


    /**
     * Extract user ID from token
     * 
     * Requirement 6.1: Provide getUserIdFromToken method that returns Long user ID
     * 
     * @param token the JWT token
     * @return user ID as Long
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extract username from token
     * 
     * Requirement 6.2: Provide getUsernameFromToken method that returns String username
     * 
     * @param token the JWT token
     * @return username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.get("username", String.class);
    }

    /**
     * Extract roles from token
     * 
     * Requirement 6.3: Provide getRolesFromToken method that returns Set of role codes
     * 
     * @param token the JWT token
     * @return set of role codes
     */
    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token) {
        Claims claims = validateToken(token);
        List<String> roles = claims.get("roles", List.class);
        return roles != null ? Set.copyOf(roles) : Set.of();
    }

    /**
     * Extract permissions from token
     * 
     * Requirement 6.4: Provide getPermissionsFromToken method that returns Set of permissions
     * 
     * @param token the JWT token
     * @return set of permissions
     */
    @SuppressWarnings("unchecked")
    public Set<String> getPermissionsFromToken(String token) {
        Claims claims = validateToken(token);
        List<String> permissions = claims.get("permissions", List.class);
        return permissions != null ? Set.copyOf(permissions) : Set.of();
    }

    /**
     * Get expiration date from token
     * 
     * Requirement 6.5: Provide getExpirationFromToken method that returns Date expiration
     * 
     * @param token the JWT token
     * @return expiration date
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = validateToken(token);
        return claims.getExpiration();
    }

    /**
     * Check if token is expired
     * 
     * Requirement 6.6: Provide isTokenExpired method that returns boolean
     * 
     * @param token the JWT token
     * @return true if expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (InvalidTokenException e) {
            return true;
        }
    }


    /**
     * Check if token is an access token
     * 
     * Requirement 7.1: Provide isAccessToken method that returns true for access tokens
     * 
     * @param token the JWT token
     * @return true if access token
     */
    public boolean isAccessToken(String token) {
        Claims claims = validateToken(token);
        return "access".equals(claims.get("type", String.class));
    }

    /**
     * Check if token is a refresh token
     * 
     * Requirement 7.2: Provide isRefreshToken method that returns true for refresh tokens
     * 
     * @param token the JWT token
     * @return true if refresh token
     */
    public boolean isRefreshToken(String token) {
        Claims claims = validateToken(token);
        return "refresh".equals(claims.get("type", String.class));
    }

    /**
     * Get the public key for external verification
     * 
     * Requirement 7.3: Provide getPublicKey method for external verification purposes
     * 
     * @return the public key
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }
}
