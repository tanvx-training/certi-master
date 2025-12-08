package com.certimaster.auth_service.security;

import com.certimaster.auth_service.service.impl.CustomUserDetailsService;
import com.certimaster.common_library.util.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token from Authorization header
            final String jwt = authHeader.substring(7);

            // Validate token
            jwtProvider.validateToken(jwt);

            // Extract email from token
            final String email = jwtProvider.getUsernameFromToken(jwt);

            // Check if user is not already authenticated
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load UserDetails
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set additional details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // If any exception occurs during token validation, continue filter chain without authentication
            // This allows the request to proceed and be handled by Spring Security's authentication entry point
            log.error("Failed to authenticate user", e);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}

