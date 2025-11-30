package com.certimaster.authservice.repository;

import com.certimaster.authservice.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    @Query("SELECT up FROM UserPermission up " +
           "JOIN FETCH up.resource r " +
           "JOIN FETCH r.feature f " +
           "JOIN FETCH f.module m " +
           "JOIN FETCH r.action a " +
           "LEFT JOIN FETCH up.dataScope ds " +
           "WHERE up.user.id = :userId " +
           "AND up.isActive = true " +
           "AND (up.expiresAt IS NULL OR up.expiresAt > :now)")
    List<UserPermission> findActiveUserPermissions(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
