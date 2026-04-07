package com.yourcompany.salesmanagement.module.auth.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType,
        String username,
        String role,
        String fullName
) {}
