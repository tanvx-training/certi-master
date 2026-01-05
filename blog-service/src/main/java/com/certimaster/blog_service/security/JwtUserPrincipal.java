package com.certimaster.blog_service.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

/**
 * Custom principal for JWT authenticated users
 * Contains user ID, username, and authorities extracted from JWT token
 */
@Getter
public class JwtUserPrincipal implements Principal {

    private final Long userId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserPrincipal(Long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
    }

    @Override
    public String getName() {
        return username;
    }

    /**
     * Check if user has a specific authority (permission or role)
     */
    public boolean hasAuthority(String authority) {
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

    /**
     * Check if user has a specific role (with ROLE_ prefix)
     */
    public boolean hasRole(String role) {
        String roleAuthority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return hasAuthority(roleAuthority);
    }
}
