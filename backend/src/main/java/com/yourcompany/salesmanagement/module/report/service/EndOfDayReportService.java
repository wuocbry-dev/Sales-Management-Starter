package com.yourcompany.salesmanagement.module.report.service;

import com.yourcompany.salesmanagement.module.report.dto.response.EndOfDayReportResponse;

import java.time.LocalDate;

public interface EndOfDayReportService {
    EndOfDayReportResponse getEndOfDay(Long branchId, LocalDate date);
}

