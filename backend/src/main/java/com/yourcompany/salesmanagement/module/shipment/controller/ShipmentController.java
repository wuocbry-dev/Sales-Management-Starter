package com.yourcompany.salesmanagement.module.shipment.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.shipment.dto.request.CreateShipmentRequest;
import com.yourcompany.salesmanagement.module.shipment.dto.request.UpdateShipmentStatusRequest;
import com.yourcompany.salesmanagement.module.shipment.dto.response.ShipmentResponse;
import com.yourcompany.salesmanagement.module.shipment.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/shipments", "/api/shipments"})
public class ShipmentController {
    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SHIPMENT_WRITE') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ShipmentResponse> create(@Valid @RequestBody CreateShipmentRequest request) {
        return BaseResponse.ok("Shipment created successfully", shipmentService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SHIPMENT_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ShipmentResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Shipment fetched successfully", shipmentService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SHIPMENT_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<ShipmentResponse>> list(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long salesOrderId) {
        if (salesOrderId != null) {
            return BaseResponse.ok("Shipments fetched successfully", shipmentService.listBySalesOrder(salesOrderId));
        }
        if (branchId != null) {
            return BaseResponse.ok("Shipments fetched successfully", shipmentService.listByBranch(branchId));
        }
        return BaseResponse.fail("branchId or salesOrderId is required", List.of());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SHIPMENT_WRITE') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ShipmentResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateShipmentStatusRequest request) {
        return BaseResponse.ok("Shipment status updated successfully", shipmentService.updateStatus(id, request));
    }
}

