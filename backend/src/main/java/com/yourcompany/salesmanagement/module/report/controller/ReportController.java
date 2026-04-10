package com.yourcompany.salesmanagement.module.report.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.dashboard.dto.response.SalesDailySummaryResponse;
import com.yourcompany.salesmanagement.module.dashboard.service.DashboardReportService;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryOverviewResponse;
import com.yourcompany.salesmanagement.module.inventory.service.InventoryOverviewService;
import com.yourcompany.salesmanagement.module.report.dto.response.EndOfDayReportResponse;
import com.yourcompany.salesmanagement.module.report.service.EndOfDayReportService;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final EndOfDayReportService endOfDayReportService;

    public ReportController(DashboardReportService dashboardReportService,
                            InventoryOverviewService inventoryOverviewService,
                            EndOfDayReportService endOfDayReportService) {
        this.dashboardReportService = dashboardReportService;
        this.inventoryOverviewService = inventoryOverviewService;
        this.endOfDayReportService = endOfDayReportService;
    }

    @GetMapping("/sales-summary")
    @PreAuthorize("hasAuthority('REPORT_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<SalesDailySummaryResponse>> salesSummary(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return BaseResponse.ok("Sales summary fetched successfully",
                dashboardReportService.getSalesDailySummary(branchId, fromDate, toDate));
    }

    @GetMapping("/inventory-summary")
    @PreAuthorize("hasAuthority('REPORT_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<InventoryOverviewResponse>> inventorySummary(@RequestParam @NotNull Long branchId) {
        return BaseResponse.ok("Inventory summary fetched successfully",
                inventoryOverviewService.getOverviewByBranch(branchId));
    }

    @GetMapping("/end-of-day")
    @PreAuthorize("hasAuthority('REPORT_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<EndOfDayReportResponse> endOfDay(
            @RequestParam @NotNull Long branchId,
            @RequestParam(required = false) LocalDate date
    ) {
        return BaseResponse.ok("OK", endOfDayReportService.getEndOfDay(branchId, date));
    }
}

