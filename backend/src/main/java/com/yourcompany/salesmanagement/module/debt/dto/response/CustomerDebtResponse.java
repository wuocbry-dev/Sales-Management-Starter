package com.yourcompany.salesmanagement.module.debt.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerDebtResponse(
        Long customerId,
        String customerCode,
        String fullName,
        String phone,
        BigDecimal totalDebt,
        long openOrdersCount,
        LocalDateTime lastOrderAt
) {}

