package com.yourcompany.salesmanagement.module.shift.repository;

import com.yourcompany.salesmanagement.module.shift.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Optional<Shift> findFirstByStoreIdAndBranchIdAndStatusOrderByOpenedAtDesc(Long storeId, Long branchId, String status);

    Optional<Shift> findByIdAndStoreId(Long id, Long storeId);
}

