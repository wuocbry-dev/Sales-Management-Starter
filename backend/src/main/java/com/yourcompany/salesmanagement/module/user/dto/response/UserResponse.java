package com.yourcompany.salesmanagement.module.user.dto.response;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String status,
        java.util.List<String> roleCodes
) {}
