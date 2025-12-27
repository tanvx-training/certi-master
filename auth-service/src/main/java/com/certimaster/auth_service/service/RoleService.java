package com.certimaster.auth_service.service;

import com.certimaster.auth_service.entity.Role;
import com.certimaster.common_library.dto.ResponseDto;

import java.util.Set;

/**
 * Service interface for role management operations.
 * 
 * Requirements:
 * - 2.3: Admin can assign/remove roles from users
 */
public interface RoleService {

    /**
     * Assigns a role to a user.
     * 
     * @param userId the ID of the user
     * @param roleCode the code of the role to assign (e.g., "ADMIN", "INSTRUCTOR", "STUDENT")
     * @return ResponseDto with success/failure status
     */
    ResponseDto<Void> assignRoleToUser(Long userId, String roleCode);

    /**
     * Removes a role from a user.
     * 
     * @param userId the ID of the user
     * @param roleCode the code of the role to remove
     * @return ResponseDto with success/failure status
     */
    ResponseDto<Void> removeRoleFromUser(Long userId, String roleCode);

    /**
     * Gets all roles assigned to a user.
     * 
     * @param userId the ID of the user
     * @return Set of roles assigned to the user
     */
    Set<Role> getUserRoles(Long userId);

    /**
     * Gets a role by its code.
     * 
     * @param roleCode the code of the role
     * @return the Role entity
     */
    Role getRoleByCode(String roleCode);
}
