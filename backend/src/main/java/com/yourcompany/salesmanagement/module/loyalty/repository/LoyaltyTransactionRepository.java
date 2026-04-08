package com.yourcompany.salesmanagement.module.loyalty.repository;

import com.yourcompany.salesmanagement.module.loyalty.entity.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, Long> {
    @Query("""
            select t
            from LoyaltyTransaction t
            where t.loyaltyAccountId = :accountId
            order by t.id desc
            """)
    List<LoyaltyTransaction> findAllByAccountIdOrderByIdDesc(@Param("accountId") Long accountId);
}

