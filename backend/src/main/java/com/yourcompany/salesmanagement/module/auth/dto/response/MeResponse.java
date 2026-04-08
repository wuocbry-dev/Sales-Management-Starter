package com.yourcompany.salesmanagement.module.auth.dto.response;

import java.util.List;

public record MeResponse(
        Long userId,
        String username,
        String fullName,
        List<String> roleCodes,
        Long storeId,
        Long branchId
) {}

