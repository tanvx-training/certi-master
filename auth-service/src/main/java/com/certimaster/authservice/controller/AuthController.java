package com.certimaster.authservice.controller;

import com.certimaster.authservice.dto.request.LoginRequest;
import com.certimaster.authservice.dto.request.RefreshTokenRequest;
import com.certimaster.authservice.dto.request.RegisterRequest;
import com.certimaster.authservice.dto.response.LoginResponse;
import com.certimaster.authservice.dto.response.RegisterResponse;
import com.certimaster.authservice.dto.response.UserResponse;
import com.certimaster.authservice.service.UserService;
import com.certimaster.commonlibrary.dto.ResponseDto;
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
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request for username: {}", loginRequest.getUsername());
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    /**
     * Get current user
     * POST /api/v1/auth/current
     */
    @GetMapping("/current")
    public ResponseEntity<ResponseDto<UserResponse>> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        log.info("Get current user request received");
        return ResponseEntity.ok(userService.getCurrent(authorization));
    }

    /**
     * Refresh access token
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<ResponseDto<LoginResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request received");
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    /**
     * Logout
     * POST /api/v1/auth/logout
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
     */
    @GetMapping("/verify-email")
    public ResponseEntity<ResponseDto<Void>> verifyEmail(@RequestParam String token) {
        log.info("Email verification request for token: {}", token);
        return ResponseEntity.ok(userService.verifyEmail(token));
    }
}
