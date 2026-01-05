package com.certimaster.exam_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility class for security-related operations
 * Provides convenient methods to access current user information from SecurityContext
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class, prevent instantiation
    }

    /**
     * Get the current authenticated user's principal
     * @return Optional containing JwtUserPrincipal if authenticated, empty otherwise
     */
    public static Optional<JwtUserPrincipal> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof JwtUserPrincipal) {
            return Optional.of((JwtUserPrincipal) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    /**
     * Get the current authenticated user's ID
     * @return Optional containing user ID if authenticated, empty otherwise
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(JwtUserPrincipal::getUserId);
    }

    /**
     * Get the current authenticated user's username
     * @return Optional containing username if authenticated, empty otherwise
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentUser().map(JwtUserPrincipal::getUsername);
    }

    /**
     * Check if the current user is authenticated
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null 
                && authentication.isAuthenticated() 
                && authentication.getPrincipal() instanceof JwtUserPrincipal;
    }

    /**
     * Check if the current user has a specific role
     * @param role the role code (without ROLE_ prefix)
     * @return true if user has the role, false otherwise
     */
    public static boolean hasRole(String role) {
        return getCurrentUser()
                .map(user -> user.hasRole(role))
                .orElse(false);
    }

    /**
     * Check if the current user has a specific permission
     * @param permission the permission string (e.g., "exam:create")
     * @return true if user has the permission, false otherwise
     */
    public static boolean hasPermission(String permission) {
        return getCurrentUser()
                .map(user -> user.hasPermission(permission))
                .orElse(false);
    }

    /**
     * Check if the current user is an admin
     * @return true if user has ADMIN role, false otherwise
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if the current user is an instructor
     * @return true if user has INSTRUCTOR role, false otherwise
     */
    public static boolean isInstructor() {
        return hasRole("INSTRUCTOR");
    }

    /**
     * Check if the current user is a student
     * @return true if user has STUDENT role, false otherwise
     */
    public static boolean isStudent() {
        return hasRole("USER");
    }
}
