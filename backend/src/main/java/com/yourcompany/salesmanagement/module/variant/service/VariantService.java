package com.yourcompany.salesmanagement.module.variant.service;

import com.yourcompany.salesmanagement.module.variant.dto.request.CreateVariantRequest;
import com.yourcompany.salesmanagement.module.variant.dto.response.VariantResponse;

import java.util.List;

public interface VariantService {
    List<VariantResponse> getVariantsByProductId(Long productId);

    VariantResponse createVariant(Long productId, CreateVariantRequest request);
}

