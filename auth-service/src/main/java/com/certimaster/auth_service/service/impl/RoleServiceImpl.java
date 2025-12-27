package com.certimaster.auth_service.service.impl;

import com.certimaster.auth_service.entity.Role;
import com.certimaster.auth_service.entity.User;
import com.certimaster.auth_service.repository.RoleRepository;
import com.certimaster.auth_service.repository.UserRepository;
import com.certimaster.auth_service.service.RoleService;
import com.certimaster.common_library.dto.ResponseDto;
import com.certimaster.common_library.exception.business.BusinessException;
import com.certimaster.common_library.exception.business.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Implementation of RoleService for managing user roles.
 * 
 * Requirements:
 * - 2.3: Admin can assign/remove roles from users, changes are persisted
 * - 2.4: Role changes reflect in new JWT tokens (handled by token generation)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public ResponseDto<Void> assignRoleToUser(Long userId, String roleCode) {
        log.info("Assigning role {} to user {}", roleCode, userId);

        // 1. Find user with roles
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> ResourceNotFoundException.byId("User", userId));

        // 2. Find role by code
        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> ResourceNotFoundException.byField("Role", "code", roleCode));

        // 3. Check if user already has this role
        if (user.hasRole(roleCode)) {
            log.warn("User {} already has role {}", userId, roleCode);
            throw new BusinessException("ROLE_ALREADY_ASSIGNED", 
                    String.format("User already has role: %s", roleCode));
        }

        // 4. Add role to user and persist (Requirement 2.3)
        user.addRole(role);
        userRepository.save(user);

        log.info("Role {} assigned to user {} successfully", roleCode, userId);
        return ResponseDto.success(
                String.format("Role %s assigned to user successfully", roleCode), 
                null
        );
    }

    @Override
    @Transactional
    public ResponseDto<Void> removeRoleFromUser(Long userId, String roleCode) {
        log.info("Removing role {} from user {}", roleCode, userId);

        // 1. Find user with roles
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> ResourceNotFoundException.byId("User", userId));

        // 2. Find role by code
        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> ResourceNotFoundException.byField("Role", "code", roleCode));

        // 3. Check if user has this role
        if (!user.hasRole(roleCode)) {
            log.warn("User {} does not have role {}", userId, roleCode);
            throw new BusinessException("ROLE_NOT_ASSIGNED", 
                    String.format("User does not have role: %s", roleCode));
        }

        // 4. Prevent removing the last role (user must have at least one role)
        if (user.getRoles().size() <= 1) {
            log.warn("Cannot remove last role from user {}", userId);
            throw new BusinessException("CANNOT_REMOVE_LAST_ROLE", 
                    "Cannot remove the last role from user. User must have at least one role.");
        }

        // 5. Remove role from user and persist (Requirement 2.3)
        user.removeRole(role);
        userRepository.save(user);

        log.info("Role {} removed from user {} successfully", roleCode, userId);
        return ResponseDto.success(
                String.format("Role %s removed from user successfully", roleCode), 
                null
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getUserRoles(Long userId) {
        log.debug("Getting roles for user {}", userId);

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> ResourceNotFoundException.byId("User", userId));

        return user.getRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleByCode(String roleCode) {
        log.debug("Getting role by code: {}", roleCode);

        return roleRepository.findByCode(roleCode)
                .orElseThrow(() -> ResourceNotFoundException.byField("Role", "code", roleCode));
    }
}
