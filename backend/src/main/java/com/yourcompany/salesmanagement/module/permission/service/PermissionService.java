package com.yourcompany.salesmanagement.module.permission.service;

import com.yourcompany.salesmanagement.module.permission.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    List<PermissionResponse> getPermissions();
}

