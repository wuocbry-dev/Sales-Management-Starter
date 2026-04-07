package com.yourcompany.salesmanagement.module.variant.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.product.repository.ProductRepository;
import com.yourcompany.salesmanagement.module.variant.dto.request.CreateVariantRequest;
import com.yourcompany.salesmanagement.module.variant.dto.response.VariantResponse;
import com.yourcompany.salesmanagement.module.variant.entity.ProductVariant;
import com.yourcompany.salesmanagement.module.variant.repository.ProductVariantRepository;
import com.yourcompany.salesmanagement.module.variant.service.VariantService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VariantServiceImpl implements VariantService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public VariantServiceImpl(ProductRepository productRepository, ProductVariantRepository productVariantRepository) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public List<VariantResponse> getVariantsByProductId(Long productId) {
        Long storeId = SecurityUtils.requireStoreId();
        productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        return productVariantRepository.findAllByProductId(productId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public VariantResponse createVariant(Long productId, CreateVariantRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        if (productVariantRepository.existsByProductIdAndSku(productId, request.sku())) {
            throw new BusinessException("Variant SKU already exists", HttpStatus.CONFLICT);
        }

        ProductVariant v = new ProductVariant();
        v.setProductId(productId);
        v.setSku(request.sku());
        v.setBarcode(request.barcode());
        v.setVariantName(request.variantName());
        v.setOption1Name(request.option1Name());
        v.setOption1Value(request.option1Value());
        v.setOption2Name(request.option2Name());
        v.setOption2Value(request.option2Value());
        v.setCostPrice(BigDecimal.valueOf(request.costPrice()));
        v.setSellingPrice(BigDecimal.valueOf(request.sellingPrice()));
        v.setStatus("ACTIVE");
        v = productVariantRepository.save(v);
        return toResponse(v);
    }

    private VariantResponse toResponse(ProductVariant v) {
        return new VariantResponse(
                v.getId(),
                v.getProductId(),
                v.getSku(),
                v.getBarcode(),
                v.getVariantName(),
                v.getCostPrice() == null ? 0.0 : v.getCostPrice().doubleValue(),
                v.getSellingPrice() == null ? 0.0 : v.getSellingPrice().doubleValue(),
                v.getStatus()
        );
    }
}

