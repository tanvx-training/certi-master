package com.certimaster.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.certimaster.auth_service.entity.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
}
