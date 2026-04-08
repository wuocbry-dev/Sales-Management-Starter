package com.yourcompany.salesmanagement.module.category.repository;

import com.yourcompany.salesmanagement.module.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findFirstByStoreIdAndNameIgnoreCase(Long storeId, String name);

    List<Category> findAllByStoreId(Long storeId);

    Optional<Category> findByIdAndStoreId(Long id, Long storeId);
}

