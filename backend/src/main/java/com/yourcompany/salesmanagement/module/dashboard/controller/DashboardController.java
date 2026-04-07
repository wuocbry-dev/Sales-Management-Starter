package com.yourcompany.salesmanagement.module.dashboard.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.dashboard.dto.response.DashboardSummaryResponse;
import com.yourcompany.salesmanagement.module.dashboard.dto.response.SalesDailySummaryResponse;
import com.yourcompany.salesmanagement.module.dashboard.service.DashboardService;
import com.yourcompany.salesmanagement.module.dashboard.service.DashboardReportService;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryOverviewResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final DashboardReportService dashboardReportService;

    public DashboardController(DashboardService dashboardService, DashboardReportService dashboardReportService) {
        this.dashboardService = dashboardService;
        this.dashboardReportService = dashboardReportService;
    }

    @GetMapping("/summary")
    public BaseResponse<DashboardSummaryResponse> getSummary() {
        return BaseResponse.ok("Dashboard summary fetched successfully", dashboardService.getSummary());
    }

    @GetMapping("/sales-daily-summary")
    public BaseResponse<List<SalesDailySummaryResponse>> salesDailySummary(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return BaseResponse.ok("Sales daily summary fetched successfully",
                dashboardReportService.getSalesDailySummary(branchId, fromDate, toDate));
    }

    @GetMapping("/inventory-warnings")
    public BaseResponse<List<InventoryOverviewResponse>> inventoryWarnings(@RequestParam Long branchId) {
        return BaseResponse.ok("Inventory warnings fetched successfully",
                dashboardReportService.getInventoryWarnings(branchId));
    }
}
