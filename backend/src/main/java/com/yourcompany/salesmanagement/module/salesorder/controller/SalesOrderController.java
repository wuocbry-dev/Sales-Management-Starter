package com.yourcompany.salesmanagement.module.salesorder.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.CreateSalesOrderRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderDetailResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderSummaryResponse;
import com.yourcompany.salesmanagement.module.salesorder.service.SalesOrderService;
import jakarta.validation.Valid;
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
    public BaseResponse<SalesOrderDetailResponse> create(@Valid @RequestBody CreateSalesOrderRequest request) {
        return BaseResponse.ok("Sales order created successfully", salesOrderService.create(request));
    }

    @GetMapping
    public BaseResponse<List<SalesOrderSummaryResponse>> list() {
        return BaseResponse.ok("Sales orders fetched successfully", salesOrderService.list());
    }

    @GetMapping("/{id}")
    public BaseResponse<SalesOrderDetailResponse> getById(@PathVariable Long id) {
        return BaseResponse.ok("Sales order fetched successfully", salesOrderService.getById(id));
    }

    @PostMapping("/{id}/complete")
    public BaseResponse<SalesOrderDetailResponse> complete(@PathVariable Long id) {
        return BaseResponse.ok("Sales order completed successfully", salesOrderService.complete(id));
    }
}

