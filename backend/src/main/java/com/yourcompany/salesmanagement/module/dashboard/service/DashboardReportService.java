package com.yourcompany.salesmanagement.module.dashboard.service;

import com.yourcompany.salesmanagement.module.dashboard.dto.response.SalesDailySummaryResponse;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryOverviewResponse;

import java.time.LocalDate;
import java.util.List;

public interface DashboardReportService {
    List<SalesDailySummaryResponse> getSalesDailySummary(Long branchId, LocalDate fromDate, LocalDate toDate);

    List<InventoryOverviewResponse> getInventoryWarnings(Long branchId);
}

