package com.yourcompany.salesmanagement.module.dashboard.service;

import com.yourcompany.salesmanagement.module.dashboard.dto.response.DashboardSummaryResponse;

public interface DashboardService {
    DashboardSummaryResponse getSummary(Long branchId);
}
