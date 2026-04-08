package com.yourcompany.salesmanagement.module.voucher.service;

import com.yourcompany.salesmanagement.module.voucher.dto.request.CreateVoucherRequest;
import com.yourcompany.salesmanagement.module.voucher.dto.request.ValidateVoucherRequest;
import com.yourcompany.salesmanagement.module.voucher.dto.response.VoucherResponse;
import com.yourcompany.salesmanagement.module.voucher.dto.response.VoucherValidationResponse;

import java.util.List;

public interface VoucherService {
    List<VoucherResponse> list();

    VoucherResponse create(CreateVoucherRequest request);

    VoucherValidationResponse validate(ValidateVoucherRequest request);
}

