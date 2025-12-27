package com.certimaster.auth_service.service.impl;

import com.certimaster.auth_service.dto.request.LoginRequest;
import com.certimaster.auth_service.dto.request.RefreshTokenRequest;
import com.certimaster.auth_service.dto.request.RegisterRequest;
import com.certimaster.auth_service.dto.response.LoginResponse;
import com.certimaster.auth_service.dto.response.RegisterResponse;
import com.certimaster.auth_service.dto.response.UserResponse;
import com.certimaster.auth_service.entity.RefreshToken;
import com.certimaster.auth_service.entity.Role;
import com.certimaster.auth_service.entity.User;
import com.certimaster.auth_service.repository.RoleRepository;
import com.certimaster.auth_service.repository.UserRepository;
import com.certimaster.auth_service.service.EmailVerificationService;
import com.certimaster.auth_service.service.PermissionService;
import com.certimaster.auth_service.service.RefreshTokenService;
import com.certimaster.auth_service.service.UserService;
import com.certimaster.common_library.dto.JwtProperties;
import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.common_library.util.JwtProvider;
import com.certimaster.common_library.enums.Status;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Simplified UserService implementation
 * Uses the new simplified entity structure (User -> Role via ManyToMany)
 * 
 * Requirements:
 * - 1.1: Register with hashed password
 * - 1.2: Reject duplicate email/username
 * - 1.3: Login returns JWT tokens
 * - 1.4: Reject invalid credentials
 * - 1.5: Refresh token returns new access token
 * - 1.6: Logout invalidates refresh token
 * - 2.2: New users get STUDENT role by default
 * - 6.1: Generate email verification token on registration
 * - 6.2, 6.3, 6.4: Email verification
 * - 7.1: BCrypt password hashing with strength 12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailVerificationService emailVerificationService;
    private final RefreshTokenService refreshTokenService;
    private final PermissionService permissionService;

    @Override
    @Transactional
    public ResponseDto<RegisterResponse> register(RegisterRequest registerRequest) {
        log.info("Starting registration process for username: {}", registerRequest.getUsername());
        
        // 1. Validate email uniqueness (Requirement 1.2)
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", registerRequest.getEmail());
            throw BusinessException.duplicateResource("User", registerRequest.getEmail());
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("Registration failed: Username already exists - {}", registerRequest.getUsername());
            throw BusinessException.duplicateResource("User", registerRequest.getUsername());
        }
        
        // 2. Hash password with BCrypt (Requirement 7.1)
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        
        // 3. Get STUDENT role (Requirement 2.2)
        var studentRole = roleRepository.findByCode("USER")
                .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", "Default USER role not found"));
        
        // 4. Create user (Requirement 1.1)
        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .passwordHash(hashedPassword)
                .fullName(registerRequest.getFullName())
                .phone(registerRequest.getPhone())
                .status(Status.ACTIVE.getDescription())
                .emailVerified(false)
                .build();
        
        // Add STUDENT role using simplified ManyToMany relationship
        user.addRole(studentRole);
        userRepository.save(user);
        
        // 5. Generate email verification token (Requirement 6.1)
        try {
            String verificationToken = emailVerificationService.createVerificationToken(user);
            emailVerificationService.sendVerificationEmail(user.getEmail(), verificationToken);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email for user: {}", user.getEmail(), e);
            // Don't fail registration if email sending fails
        }
        
        log.info("User registered successfully: {}", user.getUsername());
        return ResponseDto.success("User registered successfully", mapToRegisterResponse(user));
    }

    @Override
    @Transactional
    public ResponseDto<LoginResponse> login(LoginRequest loginRequest) {
        log.info("Starting login process for user: {}", loginRequest.getUsername());
        
        // 1. Validate credentials (Requirement 1.3, 1.4)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        // 2. Fetch user with roles
        User user = userRepository.findByEmailOrUsernameWithRoles(loginRequest.getUsername())
                .orElseThrow(UnauthorizedException::invalidCredentials);
        
        // 3. Check if account is active
        if (!Status.ACTIVE.getDescription().equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException("Account is disabled");
        }
        
        // 4. Generate JWT access token with embedded claims (Requirement 4.1)
        Set<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
        Set<String> userPermissions = user.getAllPermissions();
        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getEmail(), roles, userPermissions);
        
        // 5. Generate refresh token (Requirement 4.3)
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getUsername());
        
        // 6. Store refresh token in database
        RefreshToken rt = refreshTokenService.createRefreshToken(user, refreshToken);
        
        // 7. Load permissions for response (simplified - just Set<String>)
        Set<String> permissions = permissionService.getUserPermissions(user.getId());
        
        log.info("User logged in successfully: {}", user.getUsername());
        return ResponseDto.success("Login successful", buildLoginResponse(
                user, accessToken, rt.getToken(), permissions
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDto<LoginResponse> refreshToken(RefreshTokenRequest request) {
        log.info("Starting refresh token process");
        
        // 1. Validate refresh token (Requirement 1.5)
        User user = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        
        // 2. Fetch user with roles for token generation
        user = userRepository.findByIdWithRoles(user.getId())
                .orElseThrow(UnauthorizedException::invalidCredentials);
        
        // 3. Generate new access token with embedded claims
        Set<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
        Set<String> userPermissions = user.getAllPermissions();
        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getEmail(), roles, userPermissions);
        
        // 4. Load permissions for response (simplified - just Set<String>)
        Set<String> permissions = permissionService.getUserPermissions(user.getId());
        
        log.info("Token refreshed successfully for user: {}", user.getUsername());
        return ResponseDto.success("Token refreshed successfully", buildLoginResponse(
                user, accessToken, request.getRefreshToken(), permissions
        ));
    }

    @Override
    @Transactional
    public ResponseDto<Void> logout(String authorization) {
        log.info("Starting logout process");
        
        // 1. Extract userId from token (Requirement 1.6)
        String token = authorization.substring("Bearer ".length());
        Long userId = jwtProvider.getUserIdFromToken(token);
        
        // 2. Revoke all refresh tokens for the user
        refreshTokenService.revokeUserTokens(userId);
        
        log.info("User logged out successfully, userId: {}", userId);
        return ResponseDto.success("Logout successful", null);
    }

    @Override
    @Transactional
    public ResponseDto<Void> verifyEmail(String token) {
        log.info("Starting email verification process");
        
        // Validate token and mark email as verified (Requirements 6.2, 6.3, 6.4)
        emailVerificationService.verifyEmail(token);
        
        log.info("Email verified successfully");
        return ResponseDto.success("Email verified successfully", null);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDto<UserResponse> getCurrent(String authorization) {
        log.info("Getting current user info");
        
        String token = authorization.substring("Bearer ".length());
        Long userId = jwtProvider.getUserIdFromToken(token);
        
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(UnauthorizedException::invalidCredentials);
        
        Set<String> permissions = permissionService.getUserPermissions(userId);
        
        log.info("Current user retrieved: {}", user.getUsername());
        return ResponseDto.success("Get current user successful", buildUserResponse(user, permissions));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseDto<UserResponse> getCurrentById(Long userId) {
        log.info("Getting current user info by ID: {}", userId);
        
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(UnauthorizedException::invalidCredentials);
        
        Set<String> permissions = permissionService.getUserPermissions(userId);
        
        log.info("Current user retrieved: {}", user.getUsername());
        return ResponseDto.success("Get current user successful", buildUserResponse(user, permissions));
    }

    private RegisterResponse mapToRegisterResponse(User user) {
        log.debug("Mapping user to RegisterResponse: {}", user.getId());
        
        // Map roles from simplified ManyToMany relationship
        List<RegisterResponse.RoleInfo> roles = user.getRoles().stream()
                .map(role -> RegisterResponse.RoleInfo.builder()
                        .id(role.getId())
                        .code(role.getCode())
                        .name(role.getName())
                        .isPrimary(true) // First role is primary in simplified model
                        .build())
                .collect(Collectors.toList());

        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }

    private LoginResponse buildLoginResponse(User user, String accessToken, String refreshToken, Set<String> permissions) {
        // Build user info using simplified role structure
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatar(user.getAvatarUrl())
                .roles(user.getRoles().stream()
                        .map(role -> LoginResponse.RoleInfo.builder()
                                .id(role.getId())
                                .code(role.getCode())
                                .name(role.getName())
                                .isPrimary(true)
                                .build())
                        .collect(Collectors.toList()))
                .build();

        // Build token info with correct expiration (15 minutes = 900 seconds)
        LoginResponse.TokenInfo tokenInfo = LoginResponse.TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000) // Convert ms to seconds
                .build();

        // Build complete response
        return LoginResponse.builder()
                .user(userInfo)
                .tokens(tokenInfo)
                .permissions(permissions)
                .build();
    }

    private UserResponse buildUserResponse(User user, Set<String> permissions) {
        UserResponse.UserInfo userInfo = UserResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .username(user.getUsername())
                .status(user.getStatus())
                .phone(user.getPhone())
                .emailVerified(user.getEmailVerified())
                .roles(user.getRoles().stream()
                        .map(role -> UserResponse.RoleInfo.builder()
                                .id(role.getId())
                                .code(role.getCode())
                                .name(role.getName())
                                .isPrimary(true)
                                .build())
                        .collect(Collectors.toList()))
                .build();
        
        return UserResponse.builder()
                .user(userInfo)
                .permissions(permissions)
                .build();
    }
}
