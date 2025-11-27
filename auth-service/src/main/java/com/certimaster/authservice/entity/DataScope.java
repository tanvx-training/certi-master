package com.certimaster.authservice.entity;

import com.certimaster.commonlibrary.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "data_scopes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataScope extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer level;

    @Column(name = "filter_type", length = 50)
    private String filterType;

    @Column(name = "filter_expression", columnDefinition = "TEXT")
    private String filterExpression;
}