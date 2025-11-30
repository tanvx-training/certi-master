package com.certimaster.authservice.repository;

import com.certimaster.authservice.entity.DataScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataScopeRepository extends JpaRepository<DataScope, Long> {
}
