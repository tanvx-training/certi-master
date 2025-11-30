package com.certimaster.authservice.service.impl;

import com.certimaster.authservice.dto.response.PermissionResponse;
import com.certimaster.authservice.entity.Action;
import com.certimaster.authservice.entity.DataScope;
import com.certimaster.authservice.entity.Feature;
import com.certimaster.authservice.entity.Module;
import com.certimaster.authservice.entity.Resource;
import com.certimaster.authservice.entity.RolePermission;
import com.certimaster.authservice.entity.UserPermission;
import com.certimaster.authservice.entity.UserRole;
import com.certimaster.authservice.repository.RolePermissionRepository;
import com.certimaster.authservice.repository.UserPermissionRepository;
import com.certimaster.authservice.repository.UserRoleRepository;
import com.certimaster.authservice.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionRepository userPermissionRepository;

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse loadUserPermissions(Long userId) {
        log.info("Loading permissions for user ID: {}", userId);

        LocalDateTime now = LocalDateTime.now();

        // 1. Query UserRole to get all active roles of user
        List<UserRole> userRoles = userRoleRepository.findActiveUserRoles(userId, now);
        log.debug("Found {} active roles for user {}", userRoles.size(), userId);

        if (userRoles.isEmpty()) {
            log.warn("No active roles found for user {}", userId);
            return buildEmptyPermissionResponse();
        }

        List<Long> roleIds = userRoles.stream()
                .map(ur -> ur.getRole().getId())
                .collect(Collectors.toList());

        // 2. Query RolePermission to get permissions from roles
        List<RolePermission> rolePermissions = rolePermissionRepository.findActivePermissionsByRoleIds(roleIds, now);
        log.debug("Found {} role permissions for user {}", rolePermissions.size(), userId);

        // 3. Query UserPermission to get user-specific permissions (GRANT/DENY)
        List<UserPermission> userPermissions = userPermissionRepository.findActiveUserPermissions(userId, now);
        log.debug("Found {} user-specific permissions for user {}", userPermissions.size(), userId);

        // 4. Build PermissionResponse with modules, features, and resources
        PermissionResponse response = buildPermissionResponse(rolePermissions, userPermissions);

        log.info("Successfully loaded permissions for user {}: {} modules", userId, response.getModules().size());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, String resourceCode, String actionCode) {
        log.debug("Checking permission for user {}: resource={}, action={}", userId, resourceCode, actionCode);

        LocalDateTime now = LocalDateTime.now();

        // Get user roles
        List<UserRole> userRoles = userRoleRepository.findActiveUserRoles(userId, now);
        if (userRoles.isEmpty()) {
            return false;
        }

        List<Long> roleIds = userRoles.stream()
                .map(ur -> ur.getRole().getId())
                .collect(Collectors.toList());

        // Check role permissions
        List<RolePermission> rolePermissions = rolePermissionRepository.findActivePermissionsByRoleIds(roleIds, now);
        boolean hasRolePermission = rolePermissions.stream()
                .anyMatch(rp -> rp.getResource().getCode().equals(resourceCode) 
                        && rp.getResource().getAction().getCode().equals(actionCode));

        // Check user-specific permissions (DENY overrides GRANT)
        List<UserPermission> userPermissions = userPermissionRepository.findActiveUserPermissions(userId, now);
        
        // Check for explicit DENY
        boolean hasDeny = userPermissions.stream()
                .anyMatch(up -> up.getResource().getCode().equals(resourceCode)
                        && up.getResource().getAction().getCode().equals(actionCode)
                        && "DENY".equals(up.getPermissionType()));

        if (hasDeny) {
            log.debug("Permission denied by explicit DENY for user {}", userId);
            return false;
        }

        // Check for explicit GRANT
        boolean hasGrant = userPermissions.stream()
                .anyMatch(up -> up.getResource().getCode().equals(resourceCode)
                        && up.getResource().getAction().getCode().equals(actionCode)
                        && "GRANT".equals(up.getPermissionType()));

        boolean result = hasRolePermission || hasGrant;
        log.debug("Permission check result for user {}: {}", userId, result);
        return result;
    }

    private PermissionResponse buildEmptyPermissionResponse() {
        return PermissionResponse.builder()
                .modules(new ArrayList<>())
                .menu(new ArrayList<>())
                .quickActions(new ArrayList<>())
                .build();
    }

    private PermissionResponse buildPermissionResponse(List<RolePermission> rolePermissions, 
                                                       List<UserPermission> userPermissions) {
        // Create a map to track denied resources
        Set<String> deniedResources = userPermissions.stream()
                .filter(up -> "DENY".equals(up.getPermissionType()))
                .map(up -> up.getResource().getCode())
                .collect(Collectors.toSet());

        // Create a map to track granted resources
        Set<String> grantedResources = userPermissions.stream()
                .filter(up -> "GRANT".equals(up.getPermissionType()))
                .map(up -> up.getResource().getCode())
                .collect(Collectors.toSet());

        // Combine role permissions with user GRANT permissions
        Map<Long, Module> moduleMap = new HashMap<>();
        Map<Long, PermissionResponse.ModuleDTO> moduleDTOMap = new HashMap<>();

        // Process role permissions
        for (RolePermission rp : rolePermissions) {
            Resource resource = rp.getResource();
            
            // Skip if explicitly denied
            if (deniedResources.contains(resource.getCode())) {
                continue;
            }

            processResource(resource, rp.getDataScope(), moduleMap, moduleDTOMap);
        }

        // Process user GRANT permissions
        for (UserPermission up : userPermissions) {
            if ("GRANT".equals(up.getPermissionType())) {
                Resource resource = up.getResource();
                
                // Skip if explicitly denied (shouldn't happen, but safety check)
                if (deniedResources.contains(resource.getCode())) {
                    continue;
                }

                processResource(resource, up.getDataScope(), moduleMap, moduleDTOMap);
            }
        }

        List<PermissionResponse.ModuleDTO> modules = new ArrayList<>(moduleDTOMap.values());
        
        // Sort modules by order
        modules.sort((m1, m2) -> {
            Module mod1 = moduleMap.get(m1.getId());
            Module mod2 = moduleMap.get(m2.getId());
            return Integer.compare(
                    mod1 != null ? mod1.getOrderIndex() : 0,
                    mod2 != null ? mod2.getOrderIndex() : 0
            );
        });

        return PermissionResponse.builder()
                .modules(modules)
                .menu(buildMenuItems(modules))
                .quickActions(new ArrayList<>())
                .build();
    }

    private void processResource(Resource resource, DataScope dataScope,
                                Map<Long, Module> moduleMap,
                                Map<Long, PermissionResponse.ModuleDTO> moduleDTOMap) {
        Feature feature = resource.getFeature();
        Module module = feature.getModule();

        // Get or create ModuleDTO
        PermissionResponse.ModuleDTO moduleDTO = moduleDTOMap.computeIfAbsent(module.getId(), id -> {
            moduleMap.put(id, module);
            return PermissionResponse.ModuleDTO.builder()
                    .id(module.getId())
                    .code(module.getCode())
                    .name(module.getName())
                    .icon(module.getIcon())
                    .route(module.getRoute())
                    .features(new ArrayList<>())
                    .build();
        });

        // Find or create FeatureDTO
        PermissionResponse.FeatureDTO featureDTO = moduleDTO.getFeatures().stream()
                .filter(f -> f.getId().equals(feature.getId()))
                .findFirst()
                .orElseGet(() -> {
                    PermissionResponse.FeatureDTO newFeature = PermissionResponse.FeatureDTO.builder()
                            .id(feature.getId())
                            .code(feature.getCode())
                            .name(feature.getName())
                            .route(feature.getRoute())
                            .icon(feature.getIcon())
                            .resources(new ArrayList<>())
                            .build();
                    moduleDTO.getFeatures().add(newFeature);
                    return newFeature;
                });

        // Create ResourceDTO
        Action action = resource.getAction();
        PermissionResponse.ResourceDTO resourceDTO = PermissionResponse.ResourceDTO.builder()
                .id(resource.getId())
                .code(resource.getCode())
                .action(action.getCode())
                .scope(dataScope != null ? dataScope.getCode() : resource.getDefaultScope())
                .component(resource.getComponentType() != null ? 
                        PermissionResponse.ComponentDTO.builder()
                                .type(resource.getComponentType())
                                .key(resource.getComponentKey())
                                .build() : null)
                .apiPatterns(resource.getApiPathPattern() != null && resource.getHttpMethod() != null ?
                        List.of(PermissionResponse.ApiPatternDTO.builder()
                                .path(resource.getApiPathPattern())
                                .method(resource.getHttpMethod())
                                .build()) : new ArrayList<>())
                .build();

        // Add resource if not already present
        boolean resourceExists = featureDTO.getResources().stream()
                .anyMatch(r -> r.getId().equals(resource.getId()));
        
        if (!resourceExists) {
            featureDTO.getResources().add(resourceDTO);
        }
    }

    private List<PermissionResponse.MenuItemDTO> buildMenuItems(List<PermissionResponse.ModuleDTO> modules) {
        List<PermissionResponse.MenuItemDTO> menuItems = new ArrayList<>();

        for (PermissionResponse.ModuleDTO module : modules) {
            if (module.getFeatures().isEmpty()) {
                continue;
            }

            PermissionResponse.MenuItemDTO menuItem = PermissionResponse.MenuItemDTO.builder()
                    .id(module.getCode())
                    .title(module.getName())
                    .icon(module.getIcon())
                    .route(module.getRoute())
                    .permissions(new ArrayList<>())
                    .children(new ArrayList<>())
                    .build();

            for (PermissionResponse.FeatureDTO feature : module.getFeatures()) {
                PermissionResponse.MenuItemDTO featureMenuItem = PermissionResponse.MenuItemDTO.builder()
                        .id(feature.getCode())
                        .title(feature.getName())
                        .icon(feature.getIcon())
                        .route(feature.getRoute())
                        .permissions(feature.getResources().stream()
                                .map(PermissionResponse.ResourceDTO::getCode)
                                .collect(Collectors.toList()))
                        .children(new ArrayList<>())
                        .build();

                menuItem.getChildren().add(featureMenuItem);
            }

            menuItems.add(menuItem);
        }

        return menuItems;
    }
}
