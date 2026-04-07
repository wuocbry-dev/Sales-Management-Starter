package com.yourcompany.salesmanagement.module.supplier.service;

import com.yourcompany.salesmanagement.module.supplier.dto.request.CreateSupplierRequest;
import com.yourcompany.salesmanagement.module.supplier.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    List<SupplierResponse> getSuppliers();

    SupplierResponse createSupplier(CreateSupplierRequest request);
}

