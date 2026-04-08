package com.yourcompany.salesmanagement.module.voucher.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.voucher.dto.request.CreateVoucherRequest;
import com.yourcompany.salesmanagement.module.voucher.dto.request.ValidateVoucherRequest;
import com.yourcompany.salesmanagement.module.voucher.dto.response.VoucherResponse;
import com.yourcompany.salesmanagement.module.voucher.dto.response.VoucherValidationResponse;
import com.yourcompany.salesmanagement.module.voucher.service.VoucherService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/vouchers", "/api/vouchers"})
public class VoucherController {
    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public BaseResponse<List<VoucherResponse>> list() {
        return BaseResponse.ok("Vouchers fetched successfully", voucherService.list());
    }

    @PostMapping
    public BaseResponse<VoucherResponse> create(@Valid @RequestBody CreateVoucherRequest request) {
        return BaseResponse.ok("Voucher created successfully", voucherService.create(request));
    }

    @PostMapping("/validate")
    public BaseResponse<VoucherValidationResponse> validate(@Valid @RequestBody ValidateVoucherRequest request) {
        return BaseResponse.ok("OK", voucherService.validate(request));
    }
}

