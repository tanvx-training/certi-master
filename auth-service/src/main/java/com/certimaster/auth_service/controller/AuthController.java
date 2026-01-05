package com.certimaster.auth_service.controller;

import com.certimaster.auth_service.dto.request.LoginRequest;
import com.certimaster.auth_service.dto.request.RefreshTokenRequest;
import com.certimaster.auth_service.dto.request.RegisterRequest;
import com.certimaster.auth_service.dto.response.LoginResponse;
import com.certimaster.auth_service.dto.response.RegisterResponse;
import com.certimaster.auth_service.dto.response.UserResponse;
import com.certimaster.auth_service.security.SecurityUtils;
import com.certimaster.auth_service.service.UserService;
import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.common_library.exception.business.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    /**
     * Register new user
     * POST /api/v1/auth/register
     * Creates a new user with hashed password and returns user info
     * Requirement 1.1: Create new User with hashed password and return success response
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseDto<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Register request for username: {}", registerRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.register(registerRequest));
    }

    /**
     * Login
     * POST /api/v1/auth/login
     * Validates credentials and returns JWT access token and refresh token
     * Requirement 1.3: Return JWT access token and refresh token
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request for username: {}", loginRequest.getUsername());
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    /**
     * Get current user
     * GET /api/v1/auth/current
     * Returns user info from JWT token via SecurityContext
     * Requirement 5.2: Valid JWT populates SecurityContext with user details
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<UserResponse>> getCurrentUser() {
        log.info("Get current user request received");
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));
        return ResponseEntity.ok(userService.getCurrentById(userId));
    }

    /**
     * Refresh access token
     * POST /api/v1/auth/refresh
     * Validates refresh token and returns new access token
     * Requirement 1.5: Return new access token with valid refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request received");
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    /**
     * Logout
     * POST /api/v1/auth/logout
     * Invalidates the user's refresh token
     * Requirement 1.6: Invalidate the refresh token
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseDto<Void>> logout(@RequestHeader("Authorization") String authorization) {
        log.info("Logout request received");
        return ResponseEntity.ok(userService.logout(authorization));
    }

    /**
     * Verify email
     * GET /api/v1/auth/verify-email?token=xxx
     * Validates verification token and marks email as verified
     * Requirement 6.2: Mark email as verified with valid token
     */
    @GetMapping("/verify-email")
    public ResponseEntity<ResponseDto<Void>> verifyEmail(@RequestParam String token) {
        log.info("Email verification request for token: {}", token);
        return ResponseEntity.ok(userService.verifyEmail(token));
    }
}
