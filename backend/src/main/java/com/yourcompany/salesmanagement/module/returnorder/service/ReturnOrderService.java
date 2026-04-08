package com.yourcompany.salesmanagement.module.returnorder.service;

import com.yourcompany.salesmanagement.module.returnorder.dto.request.CreateReturnOrderRequest;
import com.yourcompany.salesmanagement.module.returnorder.dto.response.ReturnOrderResponse;

import java.util.List;

public interface ReturnOrderService {
    ReturnOrderResponse create(CreateReturnOrderRequest request);

    ReturnOrderResponse getById(Long id);

    List<ReturnOrderResponse> listBySalesOrder(Long salesOrderId);
}

