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

@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "api_path_pattern", length = 500)
    private String apiPathPattern;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "component_type", length = 50)
    private String componentType;

    @Column(name = "component_key", length = 100)
    private String componentKey;

    @Column(name = "default_scope", length = 20)
    @Builder.Default
    private String defaultScope = "OWN";

    @Column(name = "requires_approval")
    @Builder.Default
    private Boolean requiresApproval = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}