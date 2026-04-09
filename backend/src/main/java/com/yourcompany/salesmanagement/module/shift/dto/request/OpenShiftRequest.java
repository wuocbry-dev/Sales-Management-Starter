package com.yourcompany.salesmanagement.module.shift.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OpenShiftRequest(
        @NotNull Long branchId,
        BigDecimal openingCash,
        String note
) {}

