package com.certimaster.exam_service.security;

import com.certimaster.common_library.util.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JWT Authentication Filter for validating incoming requests
 * 
 * Requirements:
 * - 5.1: Provide JwtAuthenticationFilter for validating incoming requests
 * - 5.2: Populate SecurityContext with user details when request contains valid JWT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token from Authorization header
            final String jwt = authHeader.substring(BEARER_PREFIX.length());

            // Validate token and get claims
            Claims claims = jwtProvider.validateToken(jwt);

            // Check if it's an access token (not refresh token)
            String tokenType = claims.get("type", String.class);
            if (!"access".equals(tokenType)) {
                log.warn("Attempted to use non-access token for authentication");
                filterChain.doFilter(request, response);
                return;
            }

            // Extract user details from token
            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);

            // Check if user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Build authorities from roles and permissions in token
                Collection<GrantedAuthority> authorities = buildAuthorities(claims);

                // Create custom principal with user details
                JwtUserPrincipal principal = new JwtUserPrincipal(userId, username, authorities);

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        authorities
                );

                // Set additional details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Successfully authenticated user: {} with {} authorities", username, authorities.size());
            }
        } catch (Exception e) {
            // If any exception occurs during token validation, continue filter chain without authentication
            // This allows the request to proceed and be handled by Spring Security's authentication entry point
            log.debug("JWT authentication failed: {}", e.getMessage());
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Build authorities from JWT claims (roles and permissions)
     * Roles are prefixed with "ROLE_" for Spring Security compatibility
     * Permissions are added as-is for @PreAuthorize checks
     */
    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> buildAuthorities(Claims claims) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Add roles with ROLE_ prefix
        List<String> roles = claims.get("roles", List.class);
        if (roles != null) {
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }

        // Add permissions as authorities for @PreAuthorize("hasAuthority('permission')")
        List<String> permissions = claims.get("permissions", List.class);
        if (permissions != null) {
            for (String permission : permissions) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        }

        return authorities;
    }
}

