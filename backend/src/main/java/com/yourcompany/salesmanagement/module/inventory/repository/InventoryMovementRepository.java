package com.yourcompany.salesmanagement.module.inventory.repository;

import com.yourcompany.salesmanagement.module.inventory.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
}

