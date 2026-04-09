package com.yourcompany.salesmanagement.module.role.service;

import com.yourcompany.salesmanagement.module.role.dto.response.RoleResponse;
import com.yourcompany.salesmanagement.module.role.dto.request.CreateRoleRequest;

import java.util.List;

public interface RoleService {
    List<RoleResponse> getRoles();

    RoleResponse create(CreateRoleRequest request);
}

