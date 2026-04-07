package com.yourcompany.salesmanagement.module.customer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        String customerCode,
        String fullName,
        String phone,
        String email,
        String gender,
        LocalDate dateOfBirth,
        String address,
        Integer totalPoints,
        BigDecimal totalSpent,
        LocalDateTime lastOrderAt,
        String status
) {}

