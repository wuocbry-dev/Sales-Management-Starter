package com.yourcompany.salesmanagement.module.promotion.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.promotion.dto.request.CreatePromotionRequest;
import com.yourcompany.salesmanagement.module.promotion.dto.response.PromotionResponse;
import com.yourcompany.salesmanagement.module.promotion.entity.Promotion;
import com.yourcompany.salesmanagement.module.promotion.repository.PromotionRepository;
import com.yourcompany.salesmanagement.module.promotion.service.PromotionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<PromotionResponse> list() {
        Long storeId = SecurityUtils.requireStoreId();
        return promotionRepository.findAllByStoreIdOrderByIdDesc(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PromotionResponse create(CreatePromotionRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        String code = request.code().trim();
        if (promotionRepository.existsByStoreIdAndCode(storeId, code)) {
            throw new BusinessException("Promotion code already exists", HttpStatus.CONFLICT);
        }
        if (request.endAt().isBefore(request.startAt())) {
            throw new BusinessException("endAt must be >= startAt", HttpStatus.BAD_REQUEST);
        }

        Promotion p = new Promotion();
        p.setStoreId(storeId);
        p.setName(request.name().trim());
        p.setCode(code);
        p.setPromotionType(request.promotionType().trim());
        p.setValueType(request.valueType().trim());
        p.setValueAmount(money(request.valueAmount()));
        p.setMinOrderAmount(money(request.minOrderAmount()));
        p.setMaxDiscountAmount(request.maxDiscountAmount() == null ? null : money(request.maxDiscountAmount()));
        p.setStartAt(request.startAt());
        p.setEndAt(request.endAt());
        p.setStatus(request.status() == null || request.status().isBlank() ? "ACTIVE" : request.status().trim());
        p = promotionRepository.save(p);
        return toResponse(p);
    }

    private PromotionResponse toResponse(Promotion p) {
        return new PromotionResponse(
                p.getId(),
                p.getName(),
                p.getCode(),
                p.getPromotionType(),
                p.getValueType(),
                p.getValueAmount(),
                p.getMinOrderAmount(),
                p.getMaxDiscountAmount(),
                p.getStartAt(),
                p.getEndAt(),
                p.getStatus()
        );
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}

