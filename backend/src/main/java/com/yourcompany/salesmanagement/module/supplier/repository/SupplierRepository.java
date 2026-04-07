package com.yourcompany.salesmanagement.module.supplier.repository;

import com.yourcompany.salesmanagement.module.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findAllByStoreId(Long storeId);

    Optional<Supplier> findByIdAndStoreId(Long id, Long storeId);
}

