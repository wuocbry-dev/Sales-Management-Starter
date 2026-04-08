package com.yourcompany.salesmanagement.module.promotion.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.promotion.dto.request.CreatePromotionRequest;
import com.yourcompany.salesmanagement.module.promotion.dto.response.PromotionResponse;
import com.yourcompany.salesmanagement.module.promotion.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/promotions", "/api/promotions"})
public class PromotionController {
    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public BaseResponse<List<PromotionResponse>> list() {
        return BaseResponse.ok("Promotions fetched successfully", promotionService.list());
    }

    @PostMapping
    public BaseResponse<PromotionResponse> create(@Valid @RequestBody CreatePromotionRequest request) {
        return BaseResponse.ok("Promotion created successfully", promotionService.create(request));
    }
}

