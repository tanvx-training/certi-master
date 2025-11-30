package com.certimaster.authservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {

    private List<ModuleDTO> modules;
    private List<MenuItemDTO> menu;
    private List<QuickActionDTO> quickActions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ModuleDTO {
        private Long id;
        private String code;
        private String name;
        private String icon;
        private String route;
        private List<FeatureDTO> features;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeatureDTO {
        private Long id;
        private String code;
        private String name;
        private String route;
        private String icon;
        private List<ResourceDTO> resources;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResourceDTO {
        private Long id;
        private String code;
        private String action;
        private String scope;
        private ComponentDTO component;
        private List<ApiPatternDTO> apiPatterns;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComponentDTO {
        private String type;
        private String key;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiPatternDTO {
        private String path;
        private String method;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MenuItemDTO {
        private String id;
        private String title;
        private String icon;
        private String route;
        private List<String> permissions;
        private List<MenuItemDTO> children;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuickActionDTO {
        private String code;
        private String label;
        private String icon;
        private String route;
        private String componentKey;
    }
}
