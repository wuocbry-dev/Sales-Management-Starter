package com.yourcompany.salesmanagement.module.variant.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.variant.dto.request.UpdateVariantRequest;
import com.yourcompany.salesmanagement.module.variant.dto.response.VariantResponse;
import com.yourcompany.salesmanagement.module.variant.service.VariantService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/product-variants", "/api/product-variants"})
public class ProductVariantController {
    private final VariantService variantService;

    public ProductVariantController(VariantService variantService) {
        this.variantService = variantService;
    }

    @PutMapping("/{id}")
    public BaseResponse<VariantResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateVariantRequest request) {
        return BaseResponse.ok("Variant updated successfully", variantService.updateVariant(id, request));
    }
}

