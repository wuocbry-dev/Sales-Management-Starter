package com.yourcompany.salesmanagement.module.inventory.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.inventory.dto.request.InventoryAdjustRequest;
import com.yourcompany.salesmanagement.module.inventory.dto.request.UpsertInventoryThresholdRequest;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryResponse;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryThresholdResponse;
import com.yourcompany.salesmanagement.module.inventory.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/inventories", "/api/inventories"})
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('INVENTORY_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<InventoryResponse>> listByBranch(@RequestParam @NotNull Long branchId) {
        return BaseResponse.ok("Inventories fetched successfully", inventoryService.listByBranch(branchId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<InventoryResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Inventory fetched successfully", inventoryService.getById(id));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAuthority('INVENTORY_ADJUST') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<InventoryResponse> adjust(@Valid @RequestBody InventoryAdjustRequest request) {
        return BaseResponse.ok("Inventory adjusted successfully", inventoryService.adjust(request));
    }

    @GetMapping("/threshold")
    @PreAuthorize("hasAuthority('INVENTORY_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<InventoryThresholdResponse>> getThresholds(@RequestParam @NotNull Long branchId) {
        return BaseResponse.ok("Inventory thresholds fetched successfully", inventoryService.listThresholdsByBranch(branchId));
    }

    @PutMapping("/threshold")
    @PreAuthorize("hasAuthority('INVENTORY_ADJUST') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<InventoryThresholdResponse> upsertThreshold(@Valid @RequestBody UpsertInventoryThresholdRequest request) {
        return BaseResponse.ok("Inventory threshold updated successfully", inventoryService.upsertThreshold(request));
    }
}

