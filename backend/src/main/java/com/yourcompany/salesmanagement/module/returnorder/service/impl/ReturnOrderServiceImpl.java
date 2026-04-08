package com.yourcompany.salesmanagement.module.returnorder.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.cashbook.entity.CashbookEntry;
import com.yourcompany.salesmanagement.module.cashbook.repository.CashbookEntryRepository;
import com.yourcompany.salesmanagement.module.inventory.entity.Inventory;
import com.yourcompany.salesmanagement.module.inventory.repository.InventoryRepository;
import com.yourcompany.salesmanagement.module.returnorder.dto.request.CreateReturnOrderItemRequest;
import com.yourcompany.salesmanagement.module.returnorder.dto.request.CreateReturnOrderRequest;
import com.yourcompany.salesmanagement.module.returnorder.dto.response.ReturnOrderItemResponse;
import com.yourcompany.salesmanagement.module.returnorder.dto.response.ReturnOrderResponse;
import com.yourcompany.salesmanagement.module.returnorder.entity.ReturnOrder;
import com.yourcompany.salesmanagement.module.returnorder.entity.ReturnOrderItem;
import com.yourcompany.salesmanagement.module.returnorder.repository.ReturnOrderItemRepository;
import com.yourcompany.salesmanagement.module.returnorder.repository.ReturnOrderRepository;
import com.yourcompany.salesmanagement.module.returnorder.service.ReturnOrderService;
import com.yourcompany.salesmanagement.module.salesorder.entity.SalesOrder;
import com.yourcompany.salesmanagement.module.salesorder.entity.SalesOrderItem;
import com.yourcompany.salesmanagement.module.salesorder.repository.SalesOrderItemRepository;
import com.yourcompany.salesmanagement.module.salesorder.repository.SalesOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReturnOrderServiceImpl implements ReturnOrderService {

    private static final String STATUS_COMPLETED = "COMPLETED";

    private final ReturnOrderRepository returnOrderRepository;
    private final ReturnOrderItemRepository returnOrderItemRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final CashbookEntryRepository cashbookEntryRepository;

    public ReturnOrderServiceImpl(
            ReturnOrderRepository returnOrderRepository,
            ReturnOrderItemRepository returnOrderItemRepository,
            SalesOrderRepository salesOrderRepository,
            SalesOrderItemRepository salesOrderItemRepository,
            InventoryRepository inventoryRepository,
            CashbookEntryRepository cashbookEntryRepository) {
        this.returnOrderRepository = returnOrderRepository;
        this.returnOrderItemRepository = returnOrderItemRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderItemRepository = salesOrderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.cashbookEntryRepository = cashbookEntryRepository;
    }

    @Override
    @Transactional
    public ReturnOrderResponse create(CreateReturnOrderRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        UserPrincipal principal = SecurityUtils.requirePrincipal();

        SalesOrder so = salesOrderRepository.findByIdAndStoreId(request.salesOrderId(), storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        ReturnOrder ro = new ReturnOrder();
        ro.setStoreId(storeId);
        ro.setBranchId(so.getBranchId());
        ro.setSalesOrderId(so.getId());
        ro.setReturnNumber(generateReturnNumber(storeId));
        ro.setStatus(STATUS_COMPLETED);
        ro.setSubtotal(zeroMoney());
        ro.setRefundAmount(money(request.refundAmount()));
        ro.setNotes(request.notes());
        ro.setCreatedBy(principal.userId());
        ro = returnOrderRepository.save(ro);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CreateReturnOrderItemRequest it : request.items()) {
            SalesOrderItem soi = salesOrderItemRepository.findByIdAndSalesOrderId(it.salesOrderItemId(), so.getId())
                    .orElseThrow(() -> new BusinessException("Sales order item not found: " + it.salesOrderItemId(), HttpStatus.NOT_FOUND));

            BigDecimal soldQty = money(soi.getQuantity());
            BigDecimal alreadyReturned = money(returnOrderItemRepository.sumReturnedQtyBySalesOrderItemId(soi.getId()));
            BigDecimal remainingReturnable = soldQty.subtract(alreadyReturned).setScale(2, RoundingMode.HALF_UP);

            BigDecimal returnQty = money(it.quantity());
            if (returnQty.compareTo(remainingReturnable) > 0) {
                throw new BusinessException(
                        "Return quantity exceeds remaining returnable for salesOrderItemId=" + soi.getId()
                                + ". Remaining=" + remainingReturnable + ", requested=" + returnQty,
                        HttpStatus.BAD_REQUEST
                );
            }

            // add back inventory for branch
            addBackInventory(storeId, so.getBranchId(), soi.getProductId(), soi.getVariantId(), returnQty);

            BigDecimal unitPrice = money(soi.getUnitPrice());
            BigDecimal lineTotal = returnQty.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(lineTotal);

            ReturnOrderItem ri = new ReturnOrderItem();
            ri.setReturnOrderId(ro.getId());
            ri.setSalesOrderItemId(soi.getId());
            ri.setProductId(soi.getProductId());
            ri.setVariantId(soi.getVariantId());
            ri.setProductName(soi.getProductName());
            ri.setSku(soi.getSku());
            ri.setUnitPrice(unitPrice);
            ri.setQuantity(returnQty);
            ri.setLineTotal(lineTotal);
            returnOrderItemRepository.save(ri);
        }

        ro.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        returnOrderRepository.save(ro);

        // Refund policy (MVP): record money-out in cashbook if refundAmount > 0
        if (ro.getRefundAmount().compareTo(BigDecimal.ZERO) > 0) {
            CashbookEntry cb = new CashbookEntry();
            cb.setStoreId(storeId);
            cb.setBranchId(ro.getBranchId());
            cb.setEntryType("OUT");
            cb.setCategory("RETURN_REFUND");
            cb.setReferenceType("RETURN_ORDER");
            cb.setReferenceId(ro.getId());
            cb.setAmount(ro.getRefundAmount());
            cb.setDescription("Refund for return " + ro.getReturnNumber() + " (orderId=" + so.getId() + ")");
            cb.setOccurredAt(LocalDateTime.now());
            cb.setCreatedBy(principal.userId());
            cashbookEntryRepository.save(cb);
        }

        return getById(ro.getId());
    }

    @Override
    public ReturnOrderResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        ReturnOrder ro = returnOrderRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Return order not found", HttpStatus.NOT_FOUND));
        List<ReturnOrderItem> items = returnOrderItemRepository.findAllByReturnOrderId(ro.getId());
        return toResponse(ro, items);
    }

    @Override
    public List<ReturnOrderResponse> listBySalesOrder(Long salesOrderId) {
        Long storeId = SecurityUtils.requireStoreId();
        salesOrderRepository.findByIdAndStoreId(salesOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        return returnOrderRepository.findAllByStoreIdAndSalesOrderIdOrderByIdDesc(storeId, salesOrderId).stream()
                .map(ro -> toResponse(ro, returnOrderItemRepository.findAllByReturnOrderId(ro.getId())))
                .toList();
    }

    private void addBackInventory(Long storeId, Long branchId, Long productId, Long variantId, BigDecimal qty) {
        Inventory inv;
        if (variantId == null) {
            inv = inventoryRepository.findFirstByStoreIdAndBranchIdAndProductIdAndVariantIdIsNull(storeId, branchId, productId)
                    .orElseThrow(() -> new BusinessException("No inventory row for product " + productId + " at branch " + branchId, HttpStatus.BAD_REQUEST));
        } else {
            inv = inventoryRepository.findByStoreIdAndBranchIdAndProductIdAndVariantId(storeId, branchId, productId, variantId)
                    .orElseThrow(() -> new BusinessException("No inventory row for product " + productId + "/variant " + variantId + " at branch " + branchId, HttpStatus.BAD_REQUEST));
        }
        inv.setQuantity(money(inv.getQuantity()).add(qty).setScale(2, RoundingMode.HALF_UP));
        inventoryRepository.save(inv);
    }

    private ReturnOrderResponse toResponse(ReturnOrder ro, List<ReturnOrderItem> items) {
        List<ReturnOrderItemResponse> itemResponses = items.stream()
                .map(i -> new ReturnOrderItemResponse(
                        i.getId(),
                        i.getSalesOrderItemId(),
                        i.getProductId(),
                        i.getVariantId(),
                        i.getProductName(),
                        i.getSku(),
                        i.getUnitPrice(),
                        i.getQuantity(),
                        i.getLineTotal()
                ))
                .toList();

        return new ReturnOrderResponse(
                ro.getId(),
                ro.getReturnNumber(),
                ro.getStatus(),
                ro.getSalesOrderId(),
                ro.getBranchId(),
                ro.getSubtotal(),
                ro.getRefundAmount(),
                ro.getNotes(),
                ro.getCreatedBy(),
                ro.getCreatedAt(),
                itemResponses
        );
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal zeroMoney() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private String generateReturnNumber(Long storeId) {
        return "RET-" + storeId + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}

