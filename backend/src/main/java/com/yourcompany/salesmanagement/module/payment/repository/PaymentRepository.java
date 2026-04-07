package com.yourcompany.salesmanagement.module.payment.repository;

import com.yourcompany.salesmanagement.module.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByStoreIdAndSalesOrderIdOrderByIdDesc(Long storeId, Long salesOrderId);
}

