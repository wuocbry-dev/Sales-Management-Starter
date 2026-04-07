package com.yourcompany.salesmanagement.module.store.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.store.dto.response.StoreMeResponse;
import com.yourcompany.salesmanagement.module.store.repository.StoreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl implements com.yourcompany.salesmanagement.module.store.service.StoreService {
    private final StoreRepository storeRepository;

    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public StoreMeResponse getMyStore() {
        var principal = SecurityUtils.requirePrincipal();
        Long storeId = principal.storeId();
        if (storeId != null) {
            var store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new BusinessException("Store not found", HttpStatus.NOT_FOUND));
            return new StoreMeResponse(store.getId(), store.getName(), store.getCode(), store.getBusinessType());
        }

        var store = storeRepository.findFirstByOwnerUserId(principal.userId())
                .orElseThrow(() -> new BusinessException("Store not found", HttpStatus.NOT_FOUND));
        return new StoreMeResponse(store.getId(), store.getName(), store.getCode(), store.getBusinessType());
    }
}

