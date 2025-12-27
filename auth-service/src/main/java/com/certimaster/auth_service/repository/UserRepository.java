package com.certimaster.auth_service.repository;

import com.certimaster.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    /**
     * Find user by email or username (case-insensitive)
     * Requirements: 1.1, 1.3
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:identifier) OR LOWER(u.username) = LOWER(:identifier)")
    Optional<User> findByEmailOrUsername(@Param("identifier") String identifier);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Fetch user with roles eagerly loaded
     * Requirements: 1.1, 1.3
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);

    /**
     * Fetch user with roles by email or username
     * Requirements: 1.3
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE LOWER(u.email) = LOWER(:identifier) OR LOWER(u.username) = LOWER(:identifier)")
    Optional<User> findByEmailOrUsernameWithRoles(@Param("identifier") String identifier);
}
