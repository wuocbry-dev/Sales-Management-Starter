package com.yourcompany.salesmanagement.module.salesorder.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.ApplyPromotionRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.ApplyVoucherRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.CreateSalesOrderRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.InvoiceResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderDetailResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderSummaryResponse;
import com.yourcompany.salesmanagement.module.salesorder.service.SalesOrderService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/sales-orders", "/api/sales-orders"})
public class SalesOrderController {
    private final SalesOrderService salesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('POS_ORDER_CREATE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<SalesOrderDetailResponse> create(
            @RequestParam(name = "mode", required = false, defaultValue = "legacy") String mode,
            @Valid @RequestBody CreateSalesOrderRequest request) {
        if ("v2".equalsIgnoreCase(mode)) {
            return BaseResponse.ok("Sales order created (HOLD) successfully", salesOrderService.createV2Hold(request));
        }
        // Backward compatible default behavior
        return BaseResponse.ok("Sales order created successfully", salesOrderService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('POS_ORDER_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<SalesOrderSummaryResponse>> list() {
        return BaseResponse.ok("Sales orders fetched successfully", salesOrderService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('POS_ORDER_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<SalesOrderDetailResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Sales order fetched successfully", salesOrderService.getById(id));
    }

    @GetMapping("/{id}/invoice")
    @PreAuthorize("hasAuthority('POS_ORDER_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<InvoiceResponse> invoice(@PathVariable Long id) {
        return BaseResponse.ok("Invoice fetched successfully", salesOrderService.getInvoice(id));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('POS_ORDER_COMPLETE') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<SalesOrderDetailResponse> complete(@PathVariable Long id) {
        return BaseResponse.ok("Sales order completed successfully", salesOrderService.complete(id));
    }

    @PutMapping("/{id}/hold")
    @PreAuthorize("hasAuthority('POS_ORDER_HOLD') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<SalesOrderDetailResponse> hold(@PathVariable Long id) {
        return BaseResponse.ok("Sales order held successfully", salesOrderService.hold(id));
    }

    @PostMapping("/{id}/apply-voucher")
    @PreAuthorize("hasAuthority('POS_ORDER_DISCOUNT') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<SalesOrderDetailResponse> applyVoucher(@PathVariable Long id, @Valid @RequestBody ApplyVoucherRequest request) {
        return BaseResponse.ok("Voucher applied successfully", salesOrderService.applyVoucher(id, request));
    }

    @PostMapping("/{id}/apply-promotion")
    @PreAuthorize("hasAuthority('POS_ORDER_DISCOUNT') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<SalesOrderDetailResponse> applyPromotion(@PathVariable Long id, @Valid @RequestBody ApplyPromotionRequest request) {
        return BaseResponse.ok("Promotion applied successfully", salesOrderService.applyPromotion(id, request));
    }
}

