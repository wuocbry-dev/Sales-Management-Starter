package com.yourcompany.salesmanagement.module.salesorder.service;

import com.yourcompany.salesmanagement.module.salesorder.dto.request.CreateSalesOrderRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.ApplyPromotionRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.ApplyVoucherRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.InvoiceResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderDetailResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderSummaryResponse;

import java.util.List;

public interface SalesOrderService {
    SalesOrderDetailResponse create(CreateSalesOrderRequest request);

    /**
     * Workflow v2: create order in HOLD state and reserve stock (when tracking inventory).
     */
    SalesOrderDetailResponse createV2Hold(CreateSalesOrderRequest request);

    List<SalesOrderSummaryResponse> list();

    SalesOrderDetailResponse getById(Long id);

    InvoiceResponse getInvoice(Long id);

    SalesOrderDetailResponse complete(Long id);

    /**
     * Workflow v2: hold/reserve an existing PENDING order.
     */
    SalesOrderDetailResponse hold(Long id);

    SalesOrderDetailResponse applyVoucher(Long id, ApplyVoucherRequest request);

    SalesOrderDetailResponse applyPromotion(Long id, ApplyPromotionRequest request);
}

