package com.yourcompany.salesmanagement.module.inventory.service;

import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryOverviewResponse;

import java.util.List;

public interface InventoryOverviewService {
    List<InventoryOverviewResponse> getOverviewByBranch(Long branchId);
    List<InventoryOverviewResponse> getWarningsByBranch(Long branchId);
}

