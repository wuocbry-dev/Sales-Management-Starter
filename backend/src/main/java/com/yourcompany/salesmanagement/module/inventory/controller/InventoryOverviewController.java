package com.yourcompany.salesmanagement.module.inventory.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryOverviewResponse;
import com.yourcompany.salesmanagement.module.inventory.service.InventoryOverviewService;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryOverviewController {
    private final InventoryOverviewService inventoryOverviewService;

    public InventoryOverviewController(InventoryOverviewService inventoryOverviewService) {
        this.inventoryOverviewService = inventoryOverviewService;
    }

    @GetMapping("/overview")
    public BaseResponse<List<InventoryOverviewResponse>> overview(@RequestParam @NotNull Long branchId) {
        return BaseResponse.ok("Inventory overview fetched successfully", inventoryOverviewService.getOverviewByBranch(branchId));
    }
}

