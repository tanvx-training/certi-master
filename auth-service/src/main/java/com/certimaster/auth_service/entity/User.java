package com.certimaster.auth_service.entity;

import com.certimaster.common_library.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 300)
    private String email;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", length = 300)
    private String fullName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(length = 20)
    private String phone;

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserPermission> userPermissions = new HashSet<>();

    // Helper methods
    public void addUserRole(UserRole userRole) {
        userRoles.add(userRole);
        userRole.setUser(this);
    }

    public void removeUserRole(UserRole userRole) {
        userRoles.remove(userRole);
        userRole.setUser(null);
    }
}
