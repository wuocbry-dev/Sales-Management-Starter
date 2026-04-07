package com.yourcompany.salesmanagement.module.customer.repository;

import com.yourcompany.salesmanagement.module.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdAndStoreId(Long id, Long storeId);

    boolean existsByStoreIdAndCustomerCode(Long storeId, String customerCode);

    @Query("""
            select c from Customer c
            where c.storeId = :storeId
              and (
                   :q is null
                   or trim(:q) = ''
                   or lower(c.customerCode) like lower(concat('%', :q, '%'))
                   or lower(c.fullName) like lower(concat('%', :q, '%'))
                   or lower(coalesce(c.phone, '')) like lower(concat('%', :q, '%'))
                   or lower(coalesce(c.email, '')) like lower(concat('%', :q, '%'))
              )
            order by c.id desc
            """)
    List<Customer> search(@Param("storeId") Long storeId, @Param("q") String q);
}

