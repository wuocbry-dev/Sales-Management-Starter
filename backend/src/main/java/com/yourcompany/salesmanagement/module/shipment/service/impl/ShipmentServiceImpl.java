package com.yourcompany.salesmanagement.module.shipment.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import com.yourcompany.salesmanagement.module.salesorder.entity.SalesOrder;
import com.yourcompany.salesmanagement.module.salesorder.repository.SalesOrderRepository;
import com.yourcompany.salesmanagement.module.shipment.dto.request.CreateShipmentRequest;
import com.yourcompany.salesmanagement.module.shipment.dto.request.UpdateShipmentStatusRequest;
import com.yourcompany.salesmanagement.module.shipment.dto.response.ShipmentResponse;
import com.yourcompany.salesmanagement.module.shipment.entity.Shipment;
import com.yourcompany.salesmanagement.module.shipment.repository.ShipmentRepository;
import com.yourcompany.salesmanagement.module.shipment.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_SHIPPING = "SHIPPING";
    private static final String STATUS_DELIVERED = "DELIVERED";
    private static final String STATUS_FAILED = "FAILED";

    private final ShipmentRepository shipmentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final BranchRepository branchRepository;

    public ShipmentServiceImpl(ShipmentRepository shipmentRepository, SalesOrderRepository salesOrderRepository, BranchRepository branchRepository) {
        this.shipmentRepository = shipmentRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public ShipmentResponse create(CreateShipmentRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        SalesOrder so = salesOrderRepository.findByIdAndStoreId(request.salesOrderId(), storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        branchRepository.findByIdAndStoreId(so.getBranchId(), storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));

        Shipment s = new Shipment();
        s.setStoreId(storeId);
        s.setBranchId(so.getBranchId());
        s.setSalesOrderId(so.getId());
        s.setShipmentCode(generateShipmentCode(storeId));
        s.setCarrierName(request.carrierName());
        s.setServiceName(request.serviceName());
        s.setTrackingNumber(request.trackingNumber());
        s.setStatus(STATUS_PENDING);
        s.setReceiverName(request.receiverName());
        s.setReceiverPhone(request.receiverPhone());
        s.setReceiverAddress(request.receiverAddress());
        s.setShippingFee(money(request.shippingFee()));
        s.setCodAmount(money(request.codAmount()));
        s = shipmentRepository.save(s);
        return toResponse(s);
    }

    @Override
    public List<ShipmentResponse> listByBranch(Long branchId) {
        Long storeId = SecurityUtils.requireStoreId();
        return shipmentRepository.findAllByStoreIdAndBranchIdOrderByIdDesc(storeId, branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<ShipmentResponse> listBySalesOrder(Long salesOrderId) {
        Long storeId = SecurityUtils.requireStoreId();
        salesOrderRepository.findByIdAndStoreId(salesOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        return shipmentRepository.findAllByStoreIdAndSalesOrderIdOrderByIdDesc(storeId, salesOrderId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ShipmentResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        Shipment s = shipmentRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Shipment not found", HttpStatus.NOT_FOUND));
        return toResponse(s);
    }

    @Override
    public ShipmentResponse updateStatus(Long shipmentId, UpdateShipmentStatusRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Shipment s = shipmentRepository.findByIdAndStoreId(shipmentId, storeId)
                .orElseThrow(() -> new BusinessException("Shipment not found", HttpStatus.NOT_FOUND));

        String nextStatus = request.status().trim().toUpperCase();
        if (!STATUS_PENDING.equals(nextStatus)
                && !STATUS_SHIPPING.equals(nextStatus)
                && !STATUS_DELIVERED.equals(nextStatus)
                && !STATUS_FAILED.equals(nextStatus)) {
            throw new BusinessException("Unsupported shipment status. Allowed: PENDING, SHIPPING, DELIVERED, FAILED",
                    HttpStatus.BAD_REQUEST);
        }

        s.setStatus(nextStatus);

        if (request.shippedAt() != null) s.setShippedAt(request.shippedAt());
        if (request.deliveredAt() != null) s.setDeliveredAt(request.deliveredAt());

        // auto timestamps for common statuses
        if (STATUS_SHIPPING.equals(nextStatus) && s.getShippedAt() == null) {
            s.setShippedAt(LocalDateTime.now());
        }
        if (STATUS_DELIVERED.equals(nextStatus) && s.getDeliveredAt() == null) {
            s.setDeliveredAt(LocalDateTime.now());
        }

        s = shipmentRepository.save(s);
        return toResponse(s);
    }

    private ShipmentResponse toResponse(Shipment s) {
        return new ShipmentResponse(
                s.getId(),
                s.getSalesOrderId(),
                s.getShipmentCode(),
                s.getCarrierName(),
                s.getServiceName(),
                s.getTrackingNumber(),
                s.getStatus(),
                s.getReceiverName(),
                s.getReceiverPhone(),
                s.getReceiverAddress(),
                s.getShippingFee(),
                s.getCodAmount(),
                s.getShippedAt(),
                s.getDeliveredAt()
        );
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateShipmentCode(Long storeId) {
        return "SHP-" + storeId + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}

