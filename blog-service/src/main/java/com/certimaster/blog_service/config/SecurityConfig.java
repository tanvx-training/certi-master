package com.certimaster.blog_service.config;

import com.certimaster.blog_service.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Instant;
import java.util.Arrays;

/**
 * Security Configuration for Blog Service
 * 
 * Requirements:
 * - 10.1: Reject requests without author permission for post creation (403 Forbidden)
 * - 10.2: Reject edit/delete of other user's posts without admin permission (403 Forbidden)
 * - 10.3: Reject publish requests without publish permission (403 Forbidden)
 * - 10.4: Allow any authenticated user to comment or react
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Public endpoints - accessible without authentication
     */
    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/v1/posts",
            "/api/v1/posts/{slug}",
            "/api/v1/posts/{id}/view",
            "/api/v1/categories",
            "/api/v1/categories/{slug}",
            "/api/v1/categories/{slug}/posts",
            "/api/v1/tags",
            "/api/v1/tags/{slug}",
            "/api/v1/tags/{slug}/posts",
            "/api/v1/posts/{postId}/comments"
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "https://certimaster.com"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-User-Id",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
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
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Actuator and health endpoints
                        .requestMatchers(ACTUATOR_ENDPOINTS).permitAll()
                        // Swagger documentation endpoints
                        .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                        // Public GET endpoints for reading posts, categories, tags
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                        // View count increment is public
                        .requestMatchers(HttpMethod.POST, "/api/v1/posts/*/view").permitAll()
                        // All other requests require authentication
                        // Fine-grained permission checks are done at method level with @PreAuthorize
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(
                                    "{\"success\":false,\"errorCode\":\"UNAUTHORIZED\",\"message\":\"Authentication required\",\"timestamp\":\""
                                            + Instant.now() + "\"}"
                            );
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write(
                                    "{\"success\":false,\"errorCode\":\"FORBIDDEN\",\"message\":\"Access denied\",\"timestamp\":\""
                                            + Instant.now() + "\"}"
                            );
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
