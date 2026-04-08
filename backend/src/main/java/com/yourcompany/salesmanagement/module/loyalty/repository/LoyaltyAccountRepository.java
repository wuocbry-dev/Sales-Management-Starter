package com.yourcompany.salesmanagement.module.loyalty.repository;

import com.yourcompany.salesmanagement.module.loyalty.entity.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findFirstByStoreIdAndCustomerId(Long storeId, Long customerId);
}

