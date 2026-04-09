package com.yourcompany.salesmanagement.module.debt.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.debt.dto.response.CustomerDebtResponse;
import com.yourcompany.salesmanagement.module.debt.dto.response.SupplierDebtResponse;
import com.yourcompany.salesmanagement.module.debt.service.DebtService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/debts", "/api/debts"})
public class DebtController {
    private final DebtService debtService;

    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('DEBT_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<CustomerDebtResponse>> customerDebts(@RequestParam(required = false) Long branchId) {
        return BaseResponse.ok("Customer debts fetched successfully", debtService.listCustomerDebts(branchId));
    }

    @GetMapping("/suppliers")
    @PreAuthorize("hasAuthority('DEBT_READ') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<List<SupplierDebtResponse>> supplierDebts(@RequestParam(required = false) Long branchId) {
        return BaseResponse.ok("Supplier debts fetched successfully", debtService.listSupplierDebts(branchId));
    }
}

