package com.yourcompany.salesmanagement.module.purchaseorder.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.AddPurchaseOrderItemRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CancelPurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CreatePurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CreatePurchaseReturnRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.ReceivePurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseOrderDetailResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseOrderSummaryResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseReturnResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/purchase-orders", "/api/purchase-orders"})
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PURCHASE_CREATE') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<PurchaseOrderDetailResponse> create(@Valid @RequestBody CreatePurchaseOrderRequest request) {
        return BaseResponse.ok("Purchase order created successfully", purchaseOrderService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PURCHASE_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<PurchaseOrderSummaryResponse>> list() {
        return BaseResponse.ok("Purchase orders fetched successfully", purchaseOrderService.listPurchaseOrders());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PURCHASE_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<PurchaseOrderDetailResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Purchase order fetched successfully", purchaseOrderService.getById(id));
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasAuthority('PURCHASE_CREATE') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<PurchaseOrderDetailResponse> addItem(
            @PathVariable Long id,
            @Valid @RequestBody AddPurchaseOrderItemRequest request) {
        return BaseResponse.ok("Line item added successfully", purchaseOrderService.addItem(id, request));
    }

    /**
     * Post goods receipt: increases {@code inventories.quantity} for the PO's branch.
     * Body optional: empty or {@code { "lines": null }} receives all remaining quantities.
     */
    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('PURCHASE_RECEIVE') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<PurchaseOrderDetailResponse> receive(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid ReceivePurchaseOrderRequest request) {
        return BaseResponse.ok("Goods received successfully", purchaseOrderService.receive(id, request));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('PURCHASE_CANCEL') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<PurchaseOrderDetailResponse> cancel(
            @PathVariable Long id,
            @RequestBody(required = false) @Valid CancelPurchaseOrderRequest request) {
        return BaseResponse.ok("Purchase order cancelled successfully", purchaseOrderService.cancel(id, request));
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAuthority('PURCHASE_RETURN') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<PurchaseReturnResponse> createReturn(
            @PathVariable Long id,
            @Valid @RequestBody CreatePurchaseReturnRequest request) {
        return BaseResponse.ok("Purchase return created successfully", purchaseOrderService.createReturn(id, request));
    }
}
