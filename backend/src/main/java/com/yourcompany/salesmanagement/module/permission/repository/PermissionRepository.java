package com.yourcompany.salesmanagement.module.permission.repository;

import com.yourcompany.salesmanagement.module.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findAllByCodeIn(List<String> codes);

    Optional<Permission> findByCode(String code);
}


