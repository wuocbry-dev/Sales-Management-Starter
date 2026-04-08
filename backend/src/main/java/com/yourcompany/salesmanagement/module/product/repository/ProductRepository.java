package com.yourcompany.salesmanagement.module.product.repository;

import com.yourcompany.salesmanagement.module.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByStoreId(Long storeId);

    Optional<Product> findByIdAndStoreId(Long id, Long storeId);

    boolean existsByStoreIdAndSku(Long storeId, String sku);

    @Query("""
            select p
            from Product p
            where p.storeId = :storeId
              and (:categoryId is null or p.categoryId = :categoryId)
              and (:status is null or lower(p.status) = lower(:status))
              and (
                    :keyword is null
                    or lower(p.sku) like lower(concat('%', :keyword, '%'))
                    or lower(p.name) like lower(concat('%', :keyword, '%'))
                  )
            order by p.id desc
            """)
    List<Product> search(@Param("storeId") Long storeId,
                         @Param("keyword") String keyword,
                         @Param("categoryId") Long categoryId,
                         @Param("status") String status);
}

