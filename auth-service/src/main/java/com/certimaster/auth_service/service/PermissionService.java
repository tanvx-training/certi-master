package com.certimaster.auth_service.service;

import java.util.Set;

/**
 * Simplified Permission Service Interface
 * 
 * Requirements:
 * - 3.2: Return Set<String> of all user permissions
 * - 3.3: Check if permission exists in user's role permissions
 * - 8.4: Maximum 2 JOIN operations for permission queries
 */
public interface PermissionService {

    /**
     * Check if a user has a specific permission
     * 
     * @param userId the user ID
     * @param permission the permission string in format "resource:action" (e.g., "exam:create")
     * @return true if the user has the permission, false otherwise
     * 
     * Requirements: 3.3, 8.4
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * Get all permissions for a user
     * 
     * @param userId the user ID
     * @return set of permission strings in format "resource:action"
     * 
     * Requirements: 3.2
     */
    Set<String> getUserPermissions(Long userId);
}
