package com.yourcompany.salesmanagement.module.purchaseorder.service;

import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.AddPurchaseOrderItemRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CancelPurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CreatePurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CreatePurchaseReturnRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.ReceivePurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseOrderDetailResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseReturnResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseOrderSummaryResponse;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrderDetailResponse create(CreatePurchaseOrderRequest request);

    List<PurchaseOrderSummaryResponse> listPurchaseOrders();

    PurchaseOrderDetailResponse getById(Long id);

    PurchaseOrderDetailResponse addItem(Long purchaseOrderId, AddPurchaseOrderItemRequest request);

    PurchaseOrderDetailResponse receive(Long purchaseOrderId, ReceivePurchaseOrderRequest request);

    PurchaseOrderDetailResponse cancel(Long purchaseOrderId, CancelPurchaseOrderRequest request);

    PurchaseReturnResponse createReturn(Long purchaseOrderId, CreatePurchaseReturnRequest request);
}
