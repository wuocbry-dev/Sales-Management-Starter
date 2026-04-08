package com.yourcompany.salesmanagement.module.voucher.repository;

import com.yourcompany.salesmanagement.module.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findAllByStoreIdOrderByIdDesc(Long storeId);

    boolean existsByStoreIdAndCode(Long storeId, String code);

    Optional<Voucher> findByStoreIdAndCode(Long storeId, String code);
}

