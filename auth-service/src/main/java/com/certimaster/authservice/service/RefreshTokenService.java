package com.certimaster.authservice.service;

import com.certimaster.authservice.entity.RefreshToken;
import com.certimaster.authservice.entity.User;

/**
 * Service for managing refresh tokens
 */
public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user, String token);

    User validateRefreshToken(String token);

    void revokeUserTokens(Long userId);
}
