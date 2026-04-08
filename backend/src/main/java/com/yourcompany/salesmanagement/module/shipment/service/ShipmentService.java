package com.yourcompany.salesmanagement.module.shipment.service;

import com.yourcompany.salesmanagement.module.shipment.dto.request.CreateShipmentRequest;
import com.yourcompany.salesmanagement.module.shipment.dto.request.UpdateShipmentStatusRequest;
import com.yourcompany.salesmanagement.module.shipment.dto.response.ShipmentResponse;

import java.util.List;

public interface ShipmentService {
    ShipmentResponse create(CreateShipmentRequest request);

    List<ShipmentResponse> listByBranch(Long branchId);

    List<ShipmentResponse> listBySalesOrder(Long salesOrderId);

    ShipmentResponse updateStatus(Long shipmentId, UpdateShipmentStatusRequest request);
}

