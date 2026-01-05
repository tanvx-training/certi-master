package com.certimaster.exam_service.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Custom UserDetails implementation that holds user information extracted from JWT
 * Used to populate SecurityContext with user details for stateless authentication
 * 
 * Requirements:
 * - 5.2: Populate SecurityContext with user details (user ID, username, authorities)
 */
@Getter
public class JwtUserPrincipal implements UserDetails {

    private final Long userId;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;

    public JwtUserPrincipal(Long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; // Not needed for JWT-based authentication
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Check if user has a specific role
     * @param role the role code (without ROLE_ prefix)
     * @return true if user has the role
     */
    public boolean hasRole(String role) {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Check if user has a specific permission
     * @param permission the permission string (e.g., "exam:create")
     * @return true if user has the permission
     */
    public boolean hasPermission(String permission) {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(permission));
    }
}
