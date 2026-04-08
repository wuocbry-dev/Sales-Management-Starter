package com.yourcompany.salesmanagement.module.branch.repository;

import com.yourcompany.salesmanagement.module.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findFirstByStoreIdAndIsDefaultTrue(Long storeId);

    List<Branch> findAllByStoreId(Long storeId);

    Optional<Branch> findByIdAndStoreId(Long id, Long storeId);

    @Modifying
    @Query("update Branch b set b.isDefault = false where b.storeId = :storeId")
    int clearDefaultByStoreId(@Param("storeId") Long storeId);
}

