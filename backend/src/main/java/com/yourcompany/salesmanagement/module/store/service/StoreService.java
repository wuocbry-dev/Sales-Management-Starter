package com.yourcompany.salesmanagement.module.store.service;

import com.yourcompany.salesmanagement.module.store.dto.response.StoreMeResponse;
import com.yourcompany.salesmanagement.module.store.dto.request.UpdateCurrentStoreRequest;

public interface StoreService {
    StoreMeResponse getMyStore();

    StoreMeResponse updateMyStore(UpdateCurrentStoreRequest request);
}

