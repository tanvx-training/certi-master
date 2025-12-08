package com.certimaster.auth_service.entity;

import com.certimaster.common_library.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "context_type", length = 50)
    private String contextType;

    @Column(name = "context_id")
    private Long contextId;

    @Column(name = "valid_from")
    @Builder.Default
    private LocalDateTime validFrom = LocalDateTime.now();

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(name = "is_primary")
    @Builder.Default
    private Boolean isPrimary = false;
}