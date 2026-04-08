package com.yourcompany.salesmanagement.module.report.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.dashboard.dto.response.SalesDailySummaryResponse;
import com.yourcompany.salesmanagement.module.dashboard.service.DashboardReportService;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryOverviewResponse;
import com.yourcompany.salesmanagement.module.inventory.service.InventoryOverviewService;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/reports", "/api/reports"})
public class ReportController {
    private final DashboardReportService dashboardReportService;
    private final InventoryOverviewService inventoryOverviewService;

    public ReportController(DashboardReportService dashboardReportService, InventoryOverviewService inventoryOverviewService) {
        this.dashboardReportService = dashboardReportService;
        this.inventoryOverviewService = inventoryOverviewService;
    }

    @GetMapping("/sales-summary")
    public BaseResponse<List<SalesDailySummaryResponse>> salesSummary(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return BaseResponse.ok("Sales summary fetched successfully",
                dashboardReportService.getSalesDailySummary(branchId, fromDate, toDate));
    }

    @GetMapping("/inventory-summary")
    public BaseResponse<List<InventoryOverviewResponse>> inventorySummary(@RequestParam @NotNull Long branchId) {
        return BaseResponse.ok("Inventory summary fetched successfully",
                inventoryOverviewService.getOverviewByBranch(branchId));
    }
}

