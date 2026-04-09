package com.yourcompany.salesmanagement.module.debt.dto.response;

import java.math.BigDecimal;

public record SupplierDebtResponse(
        Long supplierId,
        String name,
        String phone,
        BigDecimal totalPayable,
        BigDecimal totalPaid,
        BigDecimal remainingPayable
) {}

