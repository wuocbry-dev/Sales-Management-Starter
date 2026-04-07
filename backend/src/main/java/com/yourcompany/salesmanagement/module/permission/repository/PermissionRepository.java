package com.yourcompany.salesmanagement.module.permission.repository;

import com.yourcompany.salesmanagement.module.permission.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {}

