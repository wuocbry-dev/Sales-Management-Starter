package com.yourcompany.salesmanagement.module.shipment.repository;

import com.yourcompany.salesmanagement.module.shipment.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findAllByStoreIdAndBranchIdOrderByIdDesc(Long storeId, Long branchId);

    Optional<Shipment> findByIdAndStoreId(Long id, Long storeId);

    List<Shipment> findAllByStoreIdAndSalesOrderIdOrderByIdDesc(Long storeId, Long salesOrderId);
}

