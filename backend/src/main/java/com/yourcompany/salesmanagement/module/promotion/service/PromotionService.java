package com.yourcompany.salesmanagement.module.promotion.service;

import com.yourcompany.salesmanagement.module.promotion.dto.request.CreatePromotionRequest;
import com.yourcompany.salesmanagement.module.promotion.dto.response.PromotionResponse;

import java.util.List;

public interface PromotionService {
    List<PromotionResponse> list();

    PromotionResponse create(CreatePromotionRequest request);
}

