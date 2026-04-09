package com.yourcompany.salesmanagement.module.loyalty.repository;

import com.yourcompany.salesmanagement.module.loyalty.entity.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findFirstByStoreIdAndCustomerId(Long storeId, Long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoyaltyAccount> findForUpdateFirstByStoreIdAndCustomerId(Long storeId, Long customerId);
}

