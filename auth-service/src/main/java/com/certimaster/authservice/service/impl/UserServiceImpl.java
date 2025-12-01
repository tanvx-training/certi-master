package com.certimaster.authservice.service.impl;

import com.certimaster.authservice.dto.response.UserResponse;
import com.certimaster.commonlibrary.dto.JwtProperties;
import com.certimaster.authservice.dto.request.LoginRequest;
import com.certimaster.authservice.dto.request.RefreshTokenRequest;
import com.certimaster.authservice.dto.request.RegisterRequest;
import com.certimaster.authservice.dto.response.LoginResponse;
import com.certimaster.authservice.dto.response.PermissionResponse;
import com.certimaster.authservice.dto.response.RegisterResponse;
import com.certimaster.authservice.entity.RefreshToken;
import com.certimaster.authservice.entity.Role;
import com.certimaster.authservice.entity.User;
import com.certimaster.authservice.entity.UserRole;
import com.certimaster.authservice.repository.RoleRepository;
import com.certimaster.authservice.repository.UserRepository;
import com.certimaster.authservice.service.EmailVerificationService;
import com.certimaster.authservice.service.PermissionService;
import com.certimaster.authservice.service.RefreshTokenService;
import com.certimaster.authservice.service.UserService;
import com.certimaster.commonlibrary.dto.ResponseDto;
import com.certimaster.commonlibrary.enums.Status;
import com.certimaster.commonlibrary.exception.business.BusinessException;
import com.certimaster.commonlibrary.exception.business.UnauthorizedException;
import com.certimaster.commonlibrary.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public ResponseDto<RegisterResponse> register(RegisterRequest registerRequest) {

        log.info("Starting registration process for username: {}", registerRequest.getUsername());
        // Business logic:
        // 1. Validate email uniqueness
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", registerRequest.getEmail());
            throw BusinessException.duplicateResource("User", registerRequest.getEmail());
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("Registration failed: Username already exists - {}", registerRequest.getUsername());
            throw BusinessException.duplicateResource("User", registerRequest.getUsername());
        }
        // 2. Hash password with BCrypt
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        // 3. Create user with ROLE_USER
        Role userRole = roleRepository.findByCode("USER")
                .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", "Default USER role not found"));
        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .passwordHash(hashedPassword)
                .fullName(registerRequest.getFullName())
                .phone(registerRequest.getPhone())
                .status(Status.ACTIVE.getDescription())
                .emailVerified(false)
                .build();
        UserRole userRoleAssignment = UserRole.builder()
                .role(userRole)
                .isPrimary(true)
                .build();
        user.addUserRole(userRoleAssignment);
        userRepository.save(user);
        // 4. Send verification email
        try {
            String verificationToken = emailVerificationService.createVerificationToken(user);
            emailVerificationService.sendVerificationEmail(user.getEmail(), verificationToken);
            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email for user: {}", user.getEmail(), e);
            // Don't fail registration if email sending fails
        }
        // TODO 5. Publish user.registered event to Kafka
        // 6. Return success response
        return ResponseDto.success("User registered successfully", mapToUserResponse(user));
    }

    @Override
    public ResponseDto<LoginResponse> login(LoginRequest loginRequest) {
        log.info("Starting login process for login request");
        // Business logic:
        // 1. Validate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        // 2. Check if account is active
        User user = userRepository.findByUsernameIgnoreCase(loginRequest.getUsername())
                .orElseThrow(UnauthorizedException::invalidCredentials);
        if (!Status.ACTIVE.getDescription().equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException("Account is disabled");
        }
        // 3. Generate JWT access token (15 min)
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsername());
        log.info("Processing login for user: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        claims.put("userId", user.getId());
        claims.put("roles", roles);
        claims.put("email", userDetails.getUsername());
        String accessToken = jwtProvider.generateToken(userDetails.getUsername(), claims);
        // 4. Generate refresh token (7 days)
        String refreshToken = jwtProvider.generateRefreshToken(userDetails.getUsername());
        // 5. Store refresh token in database
        RefreshToken rt = refreshTokenService.createRefreshToken(user, refreshToken);
        // TODO 6. Log audit event
        // TODO 7. Cache user info in Redis
        // 8. Return tokens
        PermissionResponse permissions = permissionService.loadUserPermissions(user.getId());
        return ResponseDto.success("Login successful", buildLoginResponse(
                user, accessToken, rt.getToken(), permissions
        ));
    }

    @Override
    public ResponseDto<LoginResponse> refreshToken(RefreshTokenRequest request) {
        log.info("Starting refresh token process for refresh token request");
        // 1. Validate refresh token
        User user = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        // 2. Generate new access token
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        log.info("Processing refresh token for user: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("userId", user.getId());
        claims.put("roles", roles);
        claims.put("email", userDetails.getUsername());
        String accessToken = jwtProvider.generateToken(userDetails.getUsername(), claims);
        // 3. Load permissions
        PermissionResponse permissions = permissionService.loadUserPermissions(user.getId());
        // 4. Build and return
        return ResponseDto.success("Token refreshed successfully", buildLoginResponse(
                user, accessToken, request.getRefreshToken(), permissions
        ));
    }

    @Override
    public ResponseDto<Void> logout(String authorization) {
        log.info("Starting logout process for logout");
        // 1. Take userId
        String token = authorization.substring("Bearer ".length());
        Long userId = jwtProvider.getUserIdFromToken(token);
        // 2. Revoke all refresh tokens
        refreshTokenService.revokeUserTokens(userId);
        return ResponseDto.success("Logout successful", null);
    }

    @Override
    public ResponseDto<Void> verifyEmail(String token) {
        log.info("Starting verify email process for verify email");
        emailVerificationService.verifyEmail(token);
        return ResponseDto.success("Verify email successful", null);
    }

    @Override
    public ResponseDto<UserResponse> getCurrent(String authorization) {
        log.info("Starting get current process for get current user");
        String token = authorization.substring("Bearer ".length());
        Long userId = jwtProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::invalidCredentials);
        PermissionResponse permissions = permissionService.loadUserPermissions(userId);
        return ResponseDto.success("Get current user successful", buildUserResponse(user, permissions));
    }

    private RegisterResponse mapToUserResponse(User user) {
        log.debug("Mapping user to UserResponse: {}", user.getId());
        
        List<RegisterResponse.RoleInfo> roles = user.getUserRoles().stream()
                .map(userRole -> RegisterResponse.RoleInfo.builder()
                        .id(userRole.getRole().getId())
                        .code(userRole.getRole().getCode())
                        .name(userRole.getRole().getName())
                        .isPrimary(userRole.getIsPrimary())
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

    private LoginResponse buildLoginResponse(User user, String accessToken, String refreshToken, PermissionResponse permissions) {
        // Build user info
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatar(user.getAvatarUrl())
                .roles(user.getUserRoles().stream()
                        .map(ur -> LoginResponse.RoleInfo.builder()
                                .id(ur.getRole().getId())
                                .code(ur.getRole().getCode())
                                .name(ur.getRole().getName())
                                .isPrimary(ur.getIsPrimary())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        // Build token info
        LoginResponse.TokenInfo tokenInfo = LoginResponse.TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration() / 1000) // Convert to seconds
                .build();

        // Build complete response
        return LoginResponse.builder()
                .user(userInfo)
                .tokens(tokenInfo)
                .permissions(permissions)
                .build();
    }

    private UserResponse buildUserResponse(User user, PermissionResponse permissions) {
        UserResponse.UserInfo userInfo = UserResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .username(user.getUsername())
                .status(user.getStatus())
                .phone(user.getPhone())
                .emailVerified(user.getEmailVerified())
                .roles(user.getUserRoles().stream()
                        .map(ur -> UserResponse.RoleInfo.builder()
                                .id(ur.getRole().getId())
                                .code(ur.getRole().getCode())
                                .name(ur.getRole().getName())
                                .isPrimary(ur.getIsPrimary())
                                .build())
                        .collect(Collectors.toList()))
                .build();
        return UserResponse.builder()
                .user(userInfo)
                .permissions(permissions)
                .build();
    }
}
