package com.yourcompany.salesmanagement.module.debt.service;

import com.yourcompany.salesmanagement.module.debt.dto.response.CustomerDebtResponse;
import com.yourcompany.salesmanagement.module.debt.dto.response.SupplierDebtResponse;

import java.util.List;

public interface DebtService {
    List<CustomerDebtResponse> listCustomerDebts(Long branchId);

    List<SupplierDebtResponse> listSupplierDebts(Long branchId);
}

