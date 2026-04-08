package com.yourcompany.salesmanagement.module.employee.repository;

import com.yourcompany.salesmanagement.module.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findFirstByUserId(Long userId);

    List<Employee> findAllByStoreId(Long storeId);

    Optional<Employee> findByIdAndStoreId(Long id, Long storeId);
}

