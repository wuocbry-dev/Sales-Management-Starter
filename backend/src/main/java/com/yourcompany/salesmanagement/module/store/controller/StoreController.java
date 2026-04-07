package com.yourcompany.salesmanagement.module.store.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.store.dto.response.StoreMeResponse;
import com.yourcompany.salesmanagement.module.store.service.StoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/me")
    public BaseResponse<StoreMeResponse> me() {
        return BaseResponse.ok("OK", storeService.getMyStore());
    }
}

