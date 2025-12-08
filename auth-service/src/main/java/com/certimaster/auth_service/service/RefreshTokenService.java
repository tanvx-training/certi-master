package com.certimaster.auth_service.service;

import com.certimaster.auth_service.entity.RefreshToken;
import com.certimaster.auth_service.entity.User;

/**
 * Service for managing refresh tokens
 */
public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user, String token);

    User validateRefreshToken(String token);

    void revokeUserTokens(Long userId);
}
