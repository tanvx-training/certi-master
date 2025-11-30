package com.certimaster.authservice.repository;

import com.certimaster.authservice.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    @Query("SELECT rp FROM RolePermission rp " +
           "JOIN FETCH rp.resource r " +
           "JOIN FETCH r.feature f " +
           "JOIN FETCH f.module m " +
           "JOIN FETCH r.action a " +
           "LEFT JOIN FETCH rp.dataScope ds " +
           "WHERE rp.role.id IN :roleIds " +
           "AND rp.isActive = true " +
           "AND (rp.expiresAt IS NULL OR rp.expiresAt > :now)")
    List<RolePermission> findActivePermissionsByRoleIds(@Param("roleIds") List<Long> roleIds, @Param("now") LocalDateTime now);
}
