package com.yourcompany.salesmanagement.module.store.repository;

import com.yourcompany.salesmanagement.module.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findFirstByOwnerUserId(Long ownerUserId);
}

