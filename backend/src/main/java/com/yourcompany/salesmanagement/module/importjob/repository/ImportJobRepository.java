package com.yourcompany.salesmanagement.module.importjob.repository;

import com.yourcompany.salesmanagement.module.importjob.entity.ImportJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImportJobRepository extends JpaRepository<ImportJob, Long> {
    Optional<ImportJob> findByIdAndStoreId(Long id, Long storeId);
}

