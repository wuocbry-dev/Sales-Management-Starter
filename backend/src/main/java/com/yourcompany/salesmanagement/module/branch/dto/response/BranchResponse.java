package com.yourcompany.salesmanagement.module.branch.dto.response;

public record BranchResponse(
        Long id,
        String name,
        String code,
        boolean isDefault,
        String status
) {}

