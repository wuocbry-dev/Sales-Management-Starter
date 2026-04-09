package com.yourcompany.salesmanagement.module.voucher.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.promotion.repository.PromotionRepository;
import com.yourcompany.salesmanagement.module.voucher.dto.request.CreateVoucherRequest;
import com.yourcompany.salesmanagement.module.voucher.dto.request.ValidateVoucherRequest;
import com.yourcompany.salesmanagement.module.voucher.dto.response.VoucherResponse;
import com.yourcompany.salesmanagement.module.voucher.dto.response.VoucherValidationResponse;
import com.yourcompany.salesmanagement.module.voucher.entity.Voucher;
import com.yourcompany.salesmanagement.module.voucher.repository.VoucherRepository;
import com.yourcompany.salesmanagement.module.voucher.service.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final PromotionRepository promotionRepository;

    public VoucherServiceImpl(VoucherRepository voucherRepository, PromotionRepository promotionRepository) {
        this.voucherRepository = voucherRepository;
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<VoucherResponse> list() {
        Long storeId = SecurityUtils.requireStoreId();
        return voucherRepository.findAllByStoreIdOrderByIdDesc(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public VoucherResponse create(CreateVoucherRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        String code = request.code().trim().toUpperCase();
        if (voucherRepository.existsByStoreIdAndCode(storeId, code)) {
            throw new BusinessException("Voucher code already exists", HttpStatus.CONFLICT);
        }
        if (request.endAt().isBefore(request.startAt())) {
            throw new BusinessException("endAt must be >= startAt", HttpStatus.BAD_REQUEST);
        }
        if (request.usageLimit() != null && request.usageLimit() <= 0) {
            throw new BusinessException("usageLimit must be > 0", HttpStatus.BAD_REQUEST);
        }

        Long promotionId = request.promotionId();
        if (promotionId != null) {
            promotionRepository.findByIdAndStoreId(promotionId, storeId)
                    .orElseThrow(() -> new BusinessException("Promotion not found", HttpStatus.NOT_FOUND));
        }

        Voucher v = new Voucher();
        v.setStoreId(storeId);
        v.setPromotionId(promotionId);
        v.setCode(code);
        v.setDiscountType(request.discountType().trim().toUpperCase());
        v.setDiscountValue(money(request.discountValue()));
        v.setMinOrderAmount(money(request.minOrderAmount()));
        v.setMaxDiscountAmount(request.maxDiscountAmount() == null ? null : money(request.maxDiscountAmount()));
        v.setUsageLimit(request.usageLimit());
        v.setUsedCount(0);
        v.setStartAt(request.startAt());
        v.setEndAt(request.endAt());
        v.setStatus(request.status() == null || request.status().isBlank() ? "ACTIVE" : request.status().trim());
        v = voucherRepository.save(v);
        return toResponse(v);
    }

    @Override
    public VoucherValidationResponse validate(ValidateVoucherRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        String code = request.code().trim().toUpperCase();
        BigDecimal orderAmount = money(request.orderAmount());

        Voucher v = voucherRepository.findByStoreIdAndCode(storeId, code)
                .orElse(null);
        if (v == null) {
            return new VoucherValidationResponse(false, "Voucher not found", code, null, null, null);
        }
        if (!"ACTIVE".equalsIgnoreCase(v.getStatus())) {
            return new VoucherValidationResponse(false, "Voucher is not active", code, v.getDiscountType(), v.getDiscountValue(), v.getMaxDiscountAmount());
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(v.getStartAt()) || now.isAfter(v.getEndAt())) {
            return new VoucherValidationResponse(false, "Voucher is not in valid time window", code, v.getDiscountType(), v.getDiscountValue(), v.getMaxDiscountAmount());
        }
        if (orderAmount.compareTo(money(v.getMinOrderAmount())) < 0) {
            return new VoucherValidationResponse(false, "Order amount is below min order amount", code, v.getDiscountType(), v.getDiscountValue(), v.getMaxDiscountAmount());
        }
        int used = v.getUsedCount() == null ? 0 : v.getUsedCount();
        int limit = v.getUsageLimit() == null ? 0 : v.getUsageLimit();
        if (limit > 0 && used >= limit) {
            return new VoucherValidationResponse(false, "Voucher usage limit exceeded", code, v.getDiscountType(), v.getDiscountValue(), v.getMaxDiscountAmount());
        }

        return new VoucherValidationResponse(true, "OK", code, v.getDiscountType(), v.getDiscountValue(), v.getMaxDiscountAmount());
    }

    private VoucherResponse toResponse(Voucher v) {
        return new VoucherResponse(
                v.getId(),
                v.getPromotionId(),
                v.getCode(),
                v.getDiscountType(),
                v.getDiscountValue(),
                v.getMinOrderAmount(),
                v.getMaxDiscountAmount(),
                v.getUsageLimit(),
                v.getUsedCount(),
                v.getStartAt(),
                v.getEndAt(),
                v.getStatus()
        );
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}

