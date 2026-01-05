package com.certimaster.blog_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility class for security-related operations
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    /**
     * Get the current authenticated user's ID
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentPrincipal().map(JwtUserPrincipal::getUserId);
    }

    /**
     * Get the current authenticated user's username
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentPrincipal().map(JwtUserPrincipal::getUsername);
    }

    /**
     * Get the current authenticated principal
     */
    public static Optional<JwtUserPrincipal> getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    /**
     * Check if current user has a specific authority
     */
    public static boolean hasAuthority(String authority) {
        return getCurrentPrincipal()
                .map(p -> p.hasAuthority(authority))
                .orElse(false);
    }

    /**
     * Check if current user has a specific role
     */
    public static boolean hasRole(String role) {
        return getCurrentPrincipal()
                .map(p -> p.hasRole(role))
                .orElse(false);
    }

    /**
     * Check if current user is authenticated
     */
    public static boolean isAuthenticated() {
        return getCurrentPrincipal().isPresent();
    }
}
