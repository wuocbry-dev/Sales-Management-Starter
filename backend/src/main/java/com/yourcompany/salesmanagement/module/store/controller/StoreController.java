package com.yourcompany.salesmanagement.module.store.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.store.dto.request.UpdateCurrentStoreRequest;
import com.yourcompany.salesmanagement.module.store.dto.response.StoreMeResponse;
import com.yourcompany.salesmanagement.module.store.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping({"/api/v1/stores", "/api/stores"})
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/me")
    public BaseResponse<StoreMeResponse> me() {
        return BaseResponse.ok("OK", storeService.getMyStore());
    }

    @GetMapping("/current")
    public BaseResponse<StoreMeResponse> current() {
        return BaseResponse.ok("OK", storeService.getMyStore());
    }

    @PutMapping("/current")
    public BaseResponse<StoreMeResponse> updateCurrent(@Valid @RequestBody UpdateCurrentStoreRequest request) {
        return BaseResponse.ok("Store updated successfully", storeService.updateMyStore(request));
    }
}

