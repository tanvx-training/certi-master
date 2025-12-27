package com.certimaster.auth_service.repository;

import com.certimaster.auth_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find refresh token by token string
     * Requirements: 1.5
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Delete all refresh tokens for a user (used during logout)
     * Requirements: 1.6
     */
    @Modifying
    void deleteByUserId(Long userId);

    /**
     * Delete refresh token by token string
     * Requirements: 1.6
     */
    @Modifying
    void deleteByToken(String token);
}
