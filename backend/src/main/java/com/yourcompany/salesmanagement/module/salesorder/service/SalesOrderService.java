package com.yourcompany.salesmanagement.module.salesorder.service;

import com.yourcompany.salesmanagement.module.salesorder.dto.request.CreateSalesOrderRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderDetailResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderSummaryResponse;

import java.util.List;

public interface SalesOrderService {
    SalesOrderDetailResponse create(CreateSalesOrderRequest request);

    List<SalesOrderSummaryResponse> list();

    SalesOrderDetailResponse getById(Long id);

    SalesOrderDetailResponse complete(Long id);
}

