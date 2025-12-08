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
public class LoginResponse {

    private UserInfo user;
    private TokenInfo tokens;
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
        private String avatar;
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Long expiresIn;
    }
}
