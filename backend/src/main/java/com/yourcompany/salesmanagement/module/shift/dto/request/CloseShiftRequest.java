package com.yourcompany.salesmanagement.module.shift.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CloseShiftRequest(
        @NotNull BigDecimal closingCash,
        String note
) {}

