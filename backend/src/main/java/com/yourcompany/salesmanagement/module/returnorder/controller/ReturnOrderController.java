package com.yourcompany.salesmanagement.module.returnorder.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.returnorder.dto.request.CreateReturnOrderRequest;
import com.yourcompany.salesmanagement.module.returnorder.dto.response.ReturnOrderResponse;
import com.yourcompany.salesmanagement.module.returnorder.service.ReturnOrderService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/returns")
public class ReturnOrderController {
    private final ReturnOrderService returnOrderService;

    public ReturnOrderController(ReturnOrderService returnOrderService) {
        this.returnOrderService = returnOrderService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('POS_ORDER_RETURN') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ReturnOrderResponse> create(@Valid @RequestBody CreateReturnOrderRequest request) {
        return BaseResponse.ok("Return created successfully", returnOrderService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('POS_ORDER_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ReturnOrderResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Return fetched successfully", returnOrderService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('POS_ORDER_READ') or hasAnyRole('ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<ReturnOrderResponse>> list(@RequestParam Long salesOrderId) {
        return BaseResponse.ok("Returns fetched successfully", returnOrderService.listBySalesOrder(salesOrderId));
    }
}

