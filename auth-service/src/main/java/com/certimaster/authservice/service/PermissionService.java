package com.certimaster.authservice.service;

import com.certimaster.authservice.dto.response.PermissionResponse;

public interface PermissionService {

    PermissionResponse loadUserPermissions(Long userId);

    boolean hasPermission(Long userId, String resourceCode, String actionCode);
}
