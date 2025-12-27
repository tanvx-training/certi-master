package com.certimaster.auth_service.service.impl;

import com.certimaster.common_library.dto.JwtProperties;
import com.certimaster.auth_service.entity.RefreshToken;
import com.certimaster.auth_service.entity.User;
import com.certimaster.auth_service.repository.RefreshTokenRepository;
import com.certimaster.auth_service.repository.UserRepository;
import com.certimaster.auth_service.service.RefreshTokenService;
import com.certimaster.common_library.exception.business.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * RefreshToken service implementation
 * Uses simplified RefreshToken entity with userId instead of User reference
 * 
 * Requirements:
 * - 1.5: Validate refresh token and return new access token
 * - 1.6: Invalidate refresh token on logout
 * - 4.3: Refresh token expiration is 7 days
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String token) {
        log.info("Creating refresh token for user: {}", user.getUsername());

        // Calculate expiration time (7 days from now)
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(token)
                .expiresAt(expiresAt)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.debug("Refresh token created with expiration: {}", expiresAt);

        return refreshToken;
    }

    @Override
    @Transactional(readOnly = true)
    public User validateRefreshToken(String token) {
        log.debug("Validating refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found");
                    return new UnauthorizedException("Invalid refresh token");
                });

        if (refreshToken.isExpired()) {
            log.warn("Refresh token expired for user ID: {}", refreshToken.getUserId());
            // Delete expired token
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        // Fetch user by userId
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> {
                    log.warn("User not found for refresh token");
                    return new UnauthorizedException("User not found");
                });

        log.debug("Refresh token validated successfully for user: {}", user.getUsername());
        return user;
    }

    @Override
    @Transactional
    public void revokeUserTokens(Long userId) {
        log.info("Revoking all refresh tokens for user ID: {}", userId);
        refreshTokenRepository.deleteByUserId(userId);
        log.debug("All refresh tokens revoked for user ID: {}", userId);
    }
}
