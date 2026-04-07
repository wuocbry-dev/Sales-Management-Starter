package com.yourcompany.salesmanagement.module.role.dto.response;

import java.util.List;

public record RoleResponse(
        Long id,
        String name,
        String code,
        String description,
        String status,
        List<String> permissionCodes
) {}

