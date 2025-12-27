package com.certimaster.auth_service.service.impl;

import com.certimaster.auth_service.entity.User;
import com.certimaster.auth_service.repository.UserRepository;
import com.certimaster.auth_service.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Simplified PermissionService implementation
 * Uses the new simplified entity structure (User -> Role -> permissions as strings)
 * 
 * Requirements:
 * - 3.2: Return Set<String> of all user permissions
 * - 3.3: Check if permission exists in user's role permissions
 * - 8.4: Maximum 2 JOIN operations for permission queries
 * 
 * Query efficiency:
 * - findByIdWithRoles uses 1 JOIN (users -> user_roles -> roles)
 * - Role.permissions uses @ElementCollection with EAGER fetch (1 additional JOIN to role_permissions)
 * - Total: 2 JOINs maximum, meeting requirement 8.4
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final UserRepository userRepository;

    /**
     * Check if a user has a specific permission
     * 
     * Implementation:
     * 1. Fetch user with roles (1 JOIN: users -> roles via user_roles)
     * 2. Roles have permissions eagerly loaded (1 JOIN: roles -> role_permissions)
     * 3. Check if permission exists in any of the user's role permissions
     * 
     * @param userId the user ID
     * @param permission the permission string in format "resource:action"
     * @return true if the user has the permission, false otherwise
     * 
     * Requirements: 3.3, 8.4
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, String permission) {
        if (userId == null || permission == null || permission.isBlank()) {
            log.warn("Invalid parameters for permission check: userId={}, permission={}", userId, permission);
            return false;
        }

        log.debug("Checking permission for user {}: {}", userId, permission);

        return userRepository.findByIdWithRoles(userId)
                .map(user -> {
                    Set<String> permissions = user.getAllPermissions();
                    boolean hasPermission = permissions.contains(permission);
                    log.debug("Permission check result for user {}: {} -> {}", userId, permission, hasPermission);
                    return hasPermission;
                })
                .orElseGet(() -> {
                    log.warn("User not found for permission check: userId={}", userId);
                    return false;
                });
    }

    /**
     * Get all permissions for a user
     * 
     * Implementation:
     * 1. Fetch user with roles (1 JOIN: users -> roles via user_roles)
     * 2. Collect all permissions from all roles (roles have permissions eagerly loaded)
     * 
     * @param userId the user ID
     * @return set of permission strings in format "resource:action"
     * 
     * Requirements: 3.2
     */
    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserPermissions(Long userId) {
        if (userId == null) {
            log.warn("Invalid userId for getUserPermissions: null");
            return Set.of();
        }

        log.debug("Getting all permissions for user {}", userId);

        return userRepository.findByIdWithRoles(userId)
                .map(User::getAllPermissions)
                .orElseGet(() -> {
                    log.warn("User not found for getUserPermissions: userId={}", userId);
                    return Set.of();
                });
    }
}
