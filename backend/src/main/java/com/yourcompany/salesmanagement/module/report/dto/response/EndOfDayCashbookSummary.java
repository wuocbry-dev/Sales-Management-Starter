package com.yourcompany.salesmanagement.module.report.dto.response;

import java.math.BigDecimal;

public record EndOfDayCashbookSummary(
        BigDecimal totalIn,
        BigDecimal totalOut
) {}

