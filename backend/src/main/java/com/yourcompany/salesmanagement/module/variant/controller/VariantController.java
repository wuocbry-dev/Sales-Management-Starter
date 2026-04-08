package com.yourcompany.salesmanagement.module.variant.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.variant.dto.request.CreateVariantRequest;
import com.yourcompany.salesmanagement.module.variant.dto.response.VariantResponse;
import com.yourcompany.salesmanagement.module.variant.service.VariantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/products/{productId}/variants", "/api/products/{productId}/variants"})
public class VariantController {
    private final VariantService variantService;

    public VariantController(VariantService variantService) {
        this.variantService = variantService;
    }

    @GetMapping
    public BaseResponse<List<VariantResponse>> getVariants(@PathVariable Long productId) {
        return BaseResponse.ok("Variants fetched successfully", variantService.getVariantsByProductId(productId));
    }

    @PostMapping
    public BaseResponse<VariantResponse> createVariant(@PathVariable Long productId, @Valid @RequestBody CreateVariantRequest request) {
        return BaseResponse.ok("Variant created successfully", variantService.createVariant(productId, request));
    }
}

