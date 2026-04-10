package com.yourcompany.salesmanagement.module.inventory.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryOverviewResponse;
import com.yourcompany.salesmanagement.module.inventory.service.InventoryOverviewService;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/inventory", "/api/inventory"})
public class InventoryOverviewController {
    private final InventoryOverviewService inventoryOverviewService;

    public InventoryOverviewController(InventoryOverviewService inventoryOverviewService) {
        this.inventoryOverviewService = inventoryOverviewService;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('INVENTORY_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<InventoryOverviewResponse>> overview(@RequestParam @NotNull Long branchId) {
        return BaseResponse.ok("Inventory overview fetched successfully", inventoryOverviewService.getOverviewByBranch(branchId));
    }

    @GetMapping("/warnings")
    @PreAuthorize("hasAuthority('INVENTORY_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<InventoryOverviewResponse>> warnings(@RequestParam @NotNull Long branchId) {
        return BaseResponse.ok("Low-stock warnings fetched successfully", inventoryOverviewService.getWarningsByBranch(branchId));
    }
}

