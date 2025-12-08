package com.certimaster.auth_service.dto.response;

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
public class UserResponse {

    private UserInfo user;
    private PermissionResponse permissions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long id;
        private String email;
        private String username;
        private String fullName;
        private String avatarUrl;
        private String phone;
        private String status;
        private Boolean emailVerified;
        private List<RoleInfo> roles;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoleInfo {
        private Long id;
        private String code;
        private String name;
        private Boolean isPrimary;
    }
}
