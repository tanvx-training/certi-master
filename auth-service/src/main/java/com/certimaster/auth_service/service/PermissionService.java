package com.certimaster.auth_service.service;

import com.certimaster.auth_service.dto.response.PermissionResponse;

public interface PermissionService {

    PermissionResponse loadUserPermissions(Long userId);

    boolean hasPermission(Long userId, String resourceCode, String actionCode);
}
