package com.yourcompany.salesmanagement.module.permission.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.permission.dto.response.PermissionResponse;
import com.yourcompany.salesmanagement.module.permission.service.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/permissions", "/api/permissions"})
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ') or hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    public BaseResponse<List<PermissionResponse>> getPermissions() {
        return BaseResponse.ok("Permissions fetched successfully", permissionService.getPermissions());
    }
}

