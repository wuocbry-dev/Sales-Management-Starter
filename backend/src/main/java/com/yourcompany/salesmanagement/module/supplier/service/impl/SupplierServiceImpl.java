package com.yourcompany.salesmanagement.module.supplier.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.supplier.dto.request.CreateSupplierRequest;
import com.yourcompany.salesmanagement.module.supplier.dto.request.UpdateSupplierRequest;
import com.yourcompany.salesmanagement.module.supplier.dto.response.SupplierResponse;
import com.yourcompany.salesmanagement.module.supplier.entity.Supplier;
import com.yourcompany.salesmanagement.module.supplier.repository.SupplierRepository;
import com.yourcompany.salesmanagement.module.supplier.service.SupplierService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public List<SupplierResponse> getSuppliers() {
        Long storeId = SecurityUtils.requireStoreId();
        return supplierRepository.findAllByStoreId(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SupplierResponse createSupplier(CreateSupplierRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Supplier s = new Supplier();
        s.setStoreId(storeId);
        s.setName(request.name());
        s.setContactName(request.contactName());
        s.setPhone(request.phone());
        s.setEmail(request.email());
        s.setAddress(request.address());
        s.setTaxCode(request.taxCode());
        s.setNotes(request.notes());
        s.setStatus("ACTIVE");
        s = supplierRepository.save(s);
        return toResponse(s);
    }

    @Override
    public SupplierResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        Supplier s = supplierRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Supplier not found", HttpStatus.NOT_FOUND));
        return toResponse(s);
    }

    @Override
    @Transactional
    public SupplierResponse update(Long id, UpdateSupplierRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Supplier s = supplierRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Supplier not found", HttpStatus.NOT_FOUND));

        s.setName(request.name());
        s.setContactName(request.contactName());
        s.setPhone(request.phone());
        s.setEmail(request.email());
        s.setAddress(request.address());
        s.setTaxCode(request.taxCode());
        s.setNotes(request.notes());
        if (request.status() != null && !request.status().isBlank()) {
            s.setStatus(request.status());
        }

        s = supplierRepository.save(s);
        return toResponse(s);
    }

    private SupplierResponse toResponse(Supplier s) {
        return new SupplierResponse(s.getId(), s.getName(), s.getContactName(), s.getPhone(), s.getEmail(), s.getStatus());
    }
}

