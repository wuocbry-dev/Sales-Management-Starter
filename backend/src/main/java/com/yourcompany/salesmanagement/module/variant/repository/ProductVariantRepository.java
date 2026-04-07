package com.yourcompany.salesmanagement.module.variant.repository;

import com.yourcompany.salesmanagement.module.variant.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findAllByProductId(Long productId);

    boolean existsByProductIdAndSku(Long productId, String sku);

    Optional<ProductVariant> findByIdAndProductId(Long id, Long productId);
}

