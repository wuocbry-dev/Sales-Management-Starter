package com.yourcompany.salesmanagement.module.user.dto.response;

import java.util.List;

public record UserDetailResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        String status,
        List<String> roleCodes
) {}

