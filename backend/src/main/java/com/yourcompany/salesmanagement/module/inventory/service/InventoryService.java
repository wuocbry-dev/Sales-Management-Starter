package com.yourcompany.salesmanagement.module.inventory.service;

import com.yourcompany.salesmanagement.module.inventory.dto.request.InventoryAdjustRequest;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> listByBranch(Long branchId);

    InventoryResponse getById(Long id);

    InventoryResponse adjust(InventoryAdjustRequest request);
}

