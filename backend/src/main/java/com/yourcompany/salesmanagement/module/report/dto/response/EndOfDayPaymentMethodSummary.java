package com.yourcompany.salesmanagement.module.report.dto.response;

import java.math.BigDecimal;

public record EndOfDayPaymentMethodSummary(
        String paymentMethod,
        BigDecimal amount
) {}

