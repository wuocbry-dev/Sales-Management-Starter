package com.yourcompany.salesmanagement.module.promotion.repository;

import com.yourcompany.salesmanagement.module.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findAllByStoreIdOrderByIdDesc(Long storeId);

    Optional<Promotion> findByIdAndStoreId(Long id, Long storeId);

    boolean existsByStoreIdAndCode(Long storeId, String code);
}

