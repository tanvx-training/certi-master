package com.certimaster.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.certimaster.authservice.entity.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
}
