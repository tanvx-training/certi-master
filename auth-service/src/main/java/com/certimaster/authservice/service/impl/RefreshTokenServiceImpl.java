package com.certimaster.authservice.service.impl;

import com.certimaster.commonlibrary.dto.JwtProperties;
import com.certimaster.authservice.entity.RefreshToken;
import com.certimaster.authservice.entity.User;
import com.certimaster.authservice.repository.RefreshTokenRepository;
import com.certimaster.authservice.service.RefreshTokenService;
import com.certimaster.commonlibrary.exception.business.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String token) {
        log.info("Creating refresh token for user: {}", user.getUsername());

        // Calculate expiration time (7 days from now)
        Instant expiresAt = Instant.now().plusMillis(jwtProperties.getRefreshExpiration());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
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
            log.warn("Refresh token expired for user: {}", refreshToken.getUser().getUsername());
            // Delete expired token
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired");
        }

        log.debug("Refresh token validated successfully for user: {}", refreshToken.getUser().getUsername());
        return refreshToken.getUser();
    }

    @Override
    @Transactional
    public void revokeUserTokens(Long userId) {
        log.info("Revoking all refresh tokens for user ID: {}", userId);
        refreshTokenRepository.deleteByUserId(userId);
        log.debug("All refresh tokens revoked for user ID: {}", userId);
    }
}
