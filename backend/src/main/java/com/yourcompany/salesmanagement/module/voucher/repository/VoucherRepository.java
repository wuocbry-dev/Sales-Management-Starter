package com.yourcompany.salesmanagement.module.voucher.repository;

import com.yourcompany.salesmanagement.module.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findAllByStoreIdOrderByIdDesc(Long storeId);

    boolean existsByStoreIdAndCode(Long storeId, String code);

    Optional<Voucher> findByStoreIdAndCode(Long storeId, String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Voucher> findForUpdateByStoreIdAndCode(Long storeId, String code);
}

