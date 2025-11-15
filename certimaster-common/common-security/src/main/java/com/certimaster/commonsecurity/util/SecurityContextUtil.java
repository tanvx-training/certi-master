package com.certimaster.commonsecurity.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class for accessing security context
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityContextUtil {

    /**
     * Get current authentication
     */
    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Get current username
     */
    public static Optional<String> getCurrentUsername() {
        return getAuthentication()
                .map(Authentication::getName);
    }

    /**
     * Get current user details
     */
    public static Optional<UserDetails> getCurrentUserDetails() {
        return getAuthentication()
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserDetails)
                .map(principal -> (UserDetails) principal);
    }

    /**
     * Get current user authorities
     */
    public static Collection<String> getCurrentAuthorities() {
        return getAuthentication()
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * Check if user has specific role
     */
    public static boolean hasRole(String role) {
        return getCurrentAuthorities().stream()
                .anyMatch(authority -> authority.equals("ROLE_" + role) || authority.equals(role));
    }

    /**
     * Check if user has any of the specified roles
     */
    public static boolean hasAnyRole(String... roles) {
        Collection<String> authorities = getCurrentAuthorities();

        for (String role : roles) {
            if (authorities.contains("ROLE_" + role) || authorities.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user has all specified roles
     */
    public static boolean hasAllRoles(String... roles) {
        Collection<String> authorities = getCurrentAuthorities();

        for (String role : roles) {
            if (!authorities.contains("ROLE_" + role) && !authorities.contains(role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        return getAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    /**
     * Check if user is anonymous
     */
    public static boolean isAnonymous() {
        return !isAuthenticated();
    }

    /**
     * Clear security context
     */
    public static void clearContext() {
        SecurityContextHolder.clearContext();
    }
}
