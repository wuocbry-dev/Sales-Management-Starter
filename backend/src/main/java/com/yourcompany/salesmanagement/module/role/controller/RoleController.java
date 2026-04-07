package com.yourcompany.salesmanagement.module.role.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.role.dto.response.RoleResponse;
import com.yourcompany.salesmanagement.module.role.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public BaseResponse<List<RoleResponse>> getRoles() {
        return BaseResponse.ok("Roles fetched successfully", roleService.getRoles());
    }
}

