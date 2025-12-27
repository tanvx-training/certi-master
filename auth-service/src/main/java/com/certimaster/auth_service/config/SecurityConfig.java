package com.certimaster.auth_service.config;

import com.certimaster.auth_service.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Instant;
import java.util.Arrays;

/**
 * Security Configuration for Auth Service
 * 
 * Requirements:
 * - 5.1: Provide JwtAuthenticationFilter for validating incoming requests
 * - 5.3: Support @PreAuthorize annotation for method-level security
 * - 5.4: Return 401 or 403 with appropriate error message for unauthorized requests
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize, @PostAuthorize, @Secured
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Public endpoints that don't require authentication
     */
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/verify-email",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password"
    };

    /**
     * Actuator and health check endpoints
     */
    private static final String[] ACTUATOR_ENDPOINTS = {
            "/actuator/**",
            "/health",
            "/health/**"
    };

    /**
     * Swagger/OpenAPI documentation endpoints
     */
    private static final String[] SWAGGER_ENDPOINTS = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (configure based on environment)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "https://certimaster.com"
        ));
        
        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-User-Id",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));
        
        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Max age for preflight requests (1 hour)
        configuration.setMaxAge(3600L);
        
        // Expose headers to client
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-User-Id"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless JWT authentication
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Stateless session management (no session cookies)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Configure endpoint authorization
                .authorizeHttpRequests(auth -> auth
                        // Public authentication endpoints
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        
                        // Actuator and health endpoints
                        .requestMatchers(ACTUATOR_ENDPOINTS).permitAll()
                        
                        // Swagger documentation endpoints
                        .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                        
                        // Protected endpoints requiring authentication
                        .requestMatchers("/api/v1/auth/logout").authenticated()
                        .requestMatchers("/api/v1/auth/current").authenticated()
                        
                        // Admin-only endpoints (example for role-based access)
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                
                // Exception handling for authentication/authorization errors
                .exceptionHandling(exception -> exception
                        // 401 Unauthorized - when authentication is required but not provided
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(
                                    "{\"success\":false,\"errorCode\":\"AUTH_004\",\"message\":\"Authentication required\",\"timestamp\":\"" 
                                    + Instant.now() + "\"}"
                            );
                        })
                        // 403 Forbidden - when authenticated but not authorized
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write(
                                    "{\"success\":false,\"errorCode\":\"AUTH_005\",\"message\":\"Access denied\",\"timestamp\":\"" 
                                    + Instant.now() + "\"}"
                            );
                        })
                )
                
                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
