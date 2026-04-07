package com.yourcompany.salesmanagement.module.loyalty.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.customer.repository.CustomerRepository;
import com.yourcompany.salesmanagement.module.loyalty.dto.response.LoyaltyAccountResponse;
import com.yourcompany.salesmanagement.module.loyalty.dto.response.LoyaltyTransactionResponse;
import com.yourcompany.salesmanagement.module.loyalty.entity.LoyaltyAccount;
import com.yourcompany.salesmanagement.module.loyalty.entity.LoyaltyTransaction;
import com.yourcompany.salesmanagement.module.loyalty.repository.LoyaltyAccountRepository;
import com.yourcompany.salesmanagement.module.loyalty.repository.LoyaltyTransactionRepository;
import com.yourcompany.salesmanagement.module.loyalty.service.LoyaltyService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final LoyaltyTransactionRepository loyaltyTransactionRepository;
    private final CustomerRepository customerRepository;

    public LoyaltyServiceImpl(
            LoyaltyAccountRepository loyaltyAccountRepository,
            LoyaltyTransactionRepository loyaltyTransactionRepository,
            CustomerRepository customerRepository) {
        this.loyaltyAccountRepository = loyaltyAccountRepository;
        this.loyaltyTransactionRepository = loyaltyTransactionRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public LoyaltyAccountResponse getAccountByCustomerId(Long customerId) {
        Long storeId = SecurityUtils.requireStoreId();
        customerRepository.findByIdAndStoreId(customerId, storeId)
                .orElseThrow(() -> new BusinessException("Customer not found", HttpStatus.NOT_FOUND));

        LoyaltyAccount acc = loyaltyAccountRepository.findFirstByStoreIdAndCustomerId(storeId, customerId)
                .orElseGet(() -> {
                    LoyaltyAccount a = new LoyaltyAccount();
                    a.setStoreId(storeId);
                    a.setCustomerId(customerId);
                    a.setCurrentPoints(0);
                    a.setLifetimePoints(0);
                    a.setTierName("SILVER");
                    return loyaltyAccountRepository.save(a);
                });

        return toAccountResponse(acc);
    }

    @Override
    public List<LoyaltyTransactionResponse> getTransactionsByCustomerId(Long customerId) {
        Long storeId = SecurityUtils.requireStoreId();
        customerRepository.findByIdAndStoreId(customerId, storeId)
                .orElseThrow(() -> new BusinessException("Customer not found", HttpStatus.NOT_FOUND));

        LoyaltyAccount acc = loyaltyAccountRepository.findFirstByStoreIdAndCustomerId(storeId, customerId)
                .orElse(null);
        if (acc == null) return List.of();

        return loyaltyTransactionRepository.findAllByAccountIdOrderByIdDesc(acc.getId()).stream()
                .map(this::toTxResponse)
                .toList();
    }

    @Override
    @Transactional
    public void earnPointsForSalesOrder(Long customerId, Long salesOrderId, int points, String description) {
        if (points <= 0) return;
        Long storeId = SecurityUtils.requireStoreId();

        customerRepository.findByIdAndStoreId(customerId, storeId)
                .orElseThrow(() -> new BusinessException("Customer not found", HttpStatus.NOT_FOUND));

        LoyaltyAccount acc = loyaltyAccountRepository.findFirstByStoreIdAndCustomerId(storeId, customerId)
                .orElseGet(() -> {
                    LoyaltyAccount a = new LoyaltyAccount();
                    a.setStoreId(storeId);
                    a.setCustomerId(customerId);
                    a.setCurrentPoints(0);
                    a.setLifetimePoints(0);
                    a.setTierName("SILVER");
                    return loyaltyAccountRepository.save(a);
                });

        acc.setCurrentPoints(acc.getCurrentPoints() + points);
        acc.setLifetimePoints(acc.getLifetimePoints() + points);
        loyaltyAccountRepository.save(acc);

        LoyaltyTransaction tx = new LoyaltyTransaction();
        tx.setLoyaltyAccountId(acc.getId());
        tx.setReferenceType("SALES_ORDER");
        tx.setReferenceId(salesOrderId);
        tx.setPointsChange(points);
        tx.setDescription(description);
        loyaltyTransactionRepository.save(tx);
    }

    private LoyaltyAccountResponse toAccountResponse(LoyaltyAccount a) {
        return new LoyaltyAccountResponse(
                a.getId(),
                a.getCustomerId(),
                a.getCurrentPoints(),
                a.getLifetimePoints(),
                a.getTierName()
        );
    }

    private LoyaltyTransactionResponse toTxResponse(LoyaltyTransaction t) {
        return new LoyaltyTransactionResponse(
                t.getId(),
                t.getReferenceType(),
                t.getReferenceId(),
                t.getPointsChange(),
                t.getDescription(),
                t.getCreatedAt()
        );
    }
}

