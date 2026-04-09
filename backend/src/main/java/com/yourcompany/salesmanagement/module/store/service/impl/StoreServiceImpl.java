package com.yourcompany.salesmanagement.module.store.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.store.dto.request.UpdateCurrentStoreRequest;
import com.yourcompany.salesmanagement.module.store.dto.response.StoreMeResponse;
import com.yourcompany.salesmanagement.module.store.entity.Store;
import com.yourcompany.salesmanagement.module.store.repository.StoreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImpl implements com.yourcompany.salesmanagement.module.store.service.StoreService {
    private final StoreRepository storeRepository;

    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public StoreMeResponse getMyStore() {
        Store store = resolveCurrentStore();
        return new StoreMeResponse(store.getId(), store.getName(), store.getCode(), store.getBusinessType());
    }

    @Override
    @Transactional
    public StoreMeResponse updateMyStore(UpdateCurrentStoreRequest request) {
        Store store = resolveCurrentStore();
        store.setName(request.name().trim());
        store.setBusinessType(request.businessType().trim());
        store = storeRepository.save(store);
        return new StoreMeResponse(store.getId(), store.getName(), store.getCode(), store.getBusinessType());
    }

    private Store resolveCurrentStore() {
        var principal = SecurityUtils.requirePrincipal();
        Long storeId = principal.storeId();
        if (storeId != null) {
            return storeRepository.findById(storeId)
                    .orElseThrow(() -> new BusinessException("Store not found", HttpStatus.NOT_FOUND));
        }

        return storeRepository.findFirstByOwnerUserId(principal.userId())
                .orElseThrow(() -> new BusinessException("Store not found", HttpStatus.NOT_FOUND));
    }
}

