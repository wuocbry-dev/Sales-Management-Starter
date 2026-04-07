package com.yourcompany.salesmanagement.module.permission.dto.response;

public record PermissionResponse(
        Long id,
        String name,
        String code,
        String moduleName,
        String description
) {}

