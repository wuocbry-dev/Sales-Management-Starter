package com.yourcompany.salesmanagement.module.dashboard.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.dashboard.dto.response.DashboardSummaryResponse;
import com.yourcompany.salesmanagement.module.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public BaseResponse<DashboardSummaryResponse> getSummary() {
        return BaseResponse.ok("Dashboard summary fetched successfully", dashboardService.getSummary());
    }
}
