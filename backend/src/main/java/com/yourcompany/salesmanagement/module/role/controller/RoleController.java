package com.yourcompany.salesmanagement.module.role.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.role.dto.request.CreateRoleRequest;
import com.yourcompany.salesmanagement.module.role.dto.response.RoleResponse;
import com.yourcompany.salesmanagement.module.role.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/roles", "/api/roles"})
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ') or hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    public BaseResponse<List<RoleResponse>> getRoles() {
        return BaseResponse.ok("Roles fetched successfully", roleService.getRoles());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_WRITE') or hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    public BaseResponse<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        return BaseResponse.ok("Role created successfully", roleService.create(request));
    }
}

