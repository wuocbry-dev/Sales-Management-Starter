package com.yourcompany.salesmanagement.module.supplier.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.supplier.dto.request.CreateSupplierRequest;
import com.yourcompany.salesmanagement.module.supplier.dto.response.SupplierResponse;
import com.yourcompany.salesmanagement.module.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public BaseResponse<List<SupplierResponse>> getSuppliers() {
        return BaseResponse.ok("Suppliers fetched successfully", supplierService.getSuppliers());
    }

    @PostMapping
    public BaseResponse<SupplierResponse> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        return BaseResponse.ok("Supplier created successfully", supplierService.createSupplier(request));
    }
}

