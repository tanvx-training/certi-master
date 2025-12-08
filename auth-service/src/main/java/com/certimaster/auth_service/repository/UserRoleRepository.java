package com.certimaster.auth_service.repository;

import com.certimaster.auth_service.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur FROM UserRole ur " +
           "JOIN FETCH ur.role r " +
           "WHERE ur.user.id = :userId " +
           "AND (ur.validFrom IS NULL OR ur.validFrom <= :now) " +
           "AND (ur.validUntil IS NULL OR ur.validUntil > :now)")
    List<UserRole> findActiveUserRoles(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
