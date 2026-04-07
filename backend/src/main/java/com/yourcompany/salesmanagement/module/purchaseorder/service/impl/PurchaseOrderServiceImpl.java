package com.yourcompany.salesmanagement.module.purchaseorder.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import com.yourcompany.salesmanagement.module.inventory.entity.Inventory;
import com.yourcompany.salesmanagement.module.inventory.repository.InventoryRepository;
import com.yourcompany.salesmanagement.module.product.repository.ProductRepository;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.AddPurchaseOrderItemRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CreatePurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.ReceiveLineRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.ReceivePurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseOrderDetailResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseOrderItemResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseOrderSummaryResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseOrder;
import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseOrderItem;
import com.yourcompany.salesmanagement.module.purchaseorder.repository.PurchaseOrderItemRepository;
import com.yourcompany.salesmanagement.module.purchaseorder.repository.PurchaseOrderRepository;
import com.yourcompany.salesmanagement.module.purchaseorder.service.PurchaseOrderService;
import com.yourcompany.salesmanagement.module.supplier.repository.SupplierRepository;
import com.yourcompany.salesmanagement.module.variant.repository.ProductVariantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_PARTIALLY_RECEIVED = "PARTIALLY_RECEIVED";
    private static final String STATUS_RECEIVED = "RECEIVED";

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;

    public PurchaseOrderServiceImpl(
            PurchaseOrderRepository purchaseOrderRepository,
            PurchaseOrderItemRepository purchaseOrderItemRepository,
            SupplierRepository supplierRepository,
            BranchRepository branchRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            InventoryRepository inventoryRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
        this.supplierRepository = supplierRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public PurchaseOrderDetailResponse create(CreatePurchaseOrderRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        UserPrincipal principal = SecurityUtils.requirePrincipal();

        supplierRepository.findByIdAndStoreId(request.supplierId(), storeId)
                .orElseThrow(() -> new BusinessException("Supplier not found", HttpStatus.NOT_FOUND));
        branchRepository.findByIdAndStoreId(request.branchId(), storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));

        PurchaseOrder po = new PurchaseOrder();
        po.setStoreId(storeId);
        po.setBranchId(request.branchId());
        po.setSupplierId(request.supplierId());
        po.setPoNumber(generatePoNumber(storeId));
        po.setOrderDate(request.orderDate() != null ? request.orderDate() : LocalDateTime.now());
        po.setExpectedDate(request.expectedDate());
        po.setStatus(STATUS_DRAFT);
        po.setSubtotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        po.setDiscountAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        po.setTaxAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        po.setTotalAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        po.setNotes(request.notes());
        po.setCreatedBy(principal.userId());
        po = purchaseOrderRepository.save(po);

        return toDetail(po, List.of());
    }

    @Override
    public List<PurchaseOrderSummaryResponse> listPurchaseOrders() {
        Long storeId = SecurityUtils.requireStoreId();
        return purchaseOrderRepository.findAllByStoreIdOrderByIdDesc(storeId).stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public PurchaseOrderDetailResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        PurchaseOrder po = purchaseOrderRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(po.getId());
        return toDetail(po, items);
    }

    @Override
    @Transactional
    public PurchaseOrderDetailResponse addItem(Long purchaseOrderId, AddPurchaseOrderItemRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        PurchaseOrder po = purchaseOrderRepository.findByIdAndStoreId(purchaseOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));

        if (!STATUS_DRAFT.equals(po.getStatus())) {
            throw new BusinessException("Items can only be added while purchase order is DRAFT", HttpStatus.BAD_REQUEST);
        }

        productRepository.findByIdAndStoreId(request.productId(), storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        Long variantId = request.variantId();
        if (variantId != null) {
            productVariantRepository.findByIdAndProductId(variantId, request.productId())
                    .orElseThrow(() -> new BusinessException("Variant not found for this product", HttpStatus.NOT_FOUND));
        }

        BigDecimal quantity = request.quantity().setScale(2, RoundingMode.HALF_UP);
        BigDecimal costPrice = request.costPrice().setScale(2, RoundingMode.HALF_UP);
        BigDecimal lineTotal = quantity.multiply(costPrice).setScale(2, RoundingMode.HALF_UP);

        PurchaseOrderItem line = new PurchaseOrderItem();
        line.setPurchaseOrderId(po.getId());
        line.setProductId(request.productId());
        line.setVariantId(variantId);
        line.setQuantity(quantity);
        line.setReceivedQuantity(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        line.setCostPrice(costPrice);
        line.setLineTotal(lineTotal);
        purchaseOrderItemRepository.save(line);

        recalculateTotals(po.getId());
        PurchaseOrder refreshed = purchaseOrderRepository.findByIdAndStoreId(po.getId(), storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(po.getId());
        return toDetail(refreshed, items);
    }

    @Override
    @Transactional
    public PurchaseOrderDetailResponse receive(Long purchaseOrderId, ReceivePurchaseOrderRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        PurchaseOrder po = purchaseOrderRepository.findByIdAndStoreId(purchaseOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));

        if (STATUS_RECEIVED.equals(po.getStatus())) {
            throw new BusinessException("Purchase order is already fully received", HttpStatus.BAD_REQUEST);
        }

        Long branchId = po.getBranchId();
        if (branchId == null) {
            throw new BusinessException("Purchase order has no branch; cannot post inventory", HttpStatus.BAD_REQUEST);
        }
        branchRepository.findByIdAndStoreId(branchId, storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));

        List<PurchaseOrderItem> allItems = purchaseOrderItemRepository.findAllByPurchaseOrderId(po.getId());
        if (allItems.isEmpty()) {
            throw new BusinessException("Purchase order has no line items", HttpStatus.BAD_REQUEST);
        }

        List<PurchaseOrderItem> toProcess;
        if (request == null || request.lines() == null || request.lines().isEmpty()) {
            toProcess = allItems;
        } else {
            toProcess = new ArrayList<>();
            Set<Long> seenItemIds = new HashSet<>();
            for (ReceiveLineRequest lineReq : request.lines()) {
                if (!seenItemIds.add(lineReq.itemId())) {
                    throw new BusinessException("Duplicate line item id in receive request: " + lineReq.itemId(), HttpStatus.BAD_REQUEST);
                }
                PurchaseOrderItem item = purchaseOrderItemRepository
                        .findByIdAndPurchaseOrderId(lineReq.itemId(), po.getId())
                        .orElseThrow(() -> new BusinessException("Line item not found: " + lineReq.itemId(), HttpStatus.NOT_FOUND));
                toProcess.add(item);
            }
        }

        for (PurchaseOrderItem item : toProcess) {
            BigDecimal remaining = item.getQuantity().subtract(item.getReceivedQuantity()).setScale(2, RoundingMode.HALF_UP);
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            BigDecimal toReceive = remaining;
            if (request != null && request.lines() != null && !request.lines().isEmpty()) {
                ReceiveLineRequest match = request.lines().stream()
                        .filter(l -> l.itemId().equals(item.getId()))
                        .findFirst()
                        .orElse(null);
                if (match != null && match.quantity() != null) {
                    toReceive = match.quantity().setScale(2, RoundingMode.HALF_UP);
                    if (toReceive.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException("Receive quantity must be > 0 for item " + item.getId(), HttpStatus.BAD_REQUEST);
                    }
                }
            }

            if (toReceive.compareTo(remaining) > 0) {
                throw new BusinessException("Cannot receive more than remaining for item " + item.getId(), HttpStatus.BAD_REQUEST);
            }

            addToInventory(storeId, branchId, item.getProductId(), item.getVariantId(), toReceive);

            item.setReceivedQuantity(item.getReceivedQuantity().add(toReceive).setScale(2, RoundingMode.HALF_UP));
            purchaseOrderItemRepository.save(item);
        }

        refreshReceivedStatus(po.getId());
        PurchaseOrder refreshed = purchaseOrderRepository.findByIdAndStoreId(purchaseOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(po.getId());
        return toDetail(refreshed, items);
    }

    private void addToInventory(Long storeId, Long branchId, Long productId, Long variantId, BigDecimal delta) {
        Inventory inv;
        if (variantId == null) {
            inv = inventoryRepository
                    .findFirstByStoreIdAndBranchIdAndProductIdAndVariantIdIsNull(storeId, branchId, productId)
                    .orElseGet(() -> newInventoryRow(storeId, branchId, productId, null));
        } else {
            inv = inventoryRepository
                    .findByStoreIdAndBranchIdAndProductIdAndVariantId(storeId, branchId, productId, variantId)
                    .orElseGet(() -> newInventoryRow(storeId, branchId, productId, variantId));
        }

        BigDecimal current = inv.getQuantity() == null ? BigDecimal.ZERO : inv.getQuantity();
        inv.setQuantity(current.add(delta).setScale(2, RoundingMode.HALF_UP));
        inventoryRepository.save(inv);
    }

    private Inventory newInventoryRow(Long storeId, Long branchId, Long productId, Long variantId) {
        Inventory i = new Inventory();
        i.setStoreId(storeId);
        i.setBranchId(branchId);
        i.setProductId(productId);
        i.setVariantId(variantId);
        i.setQuantity(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        i.setReservedQuantity(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        i.setMinQuantity(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        i.setMaxQuantity(null);
        return i;
    }

    private void refreshReceivedStatus(Long purchaseOrderId) {
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(purchaseOrderId);
        boolean anyReceived = items.stream().anyMatch(i -> i.getReceivedQuantity().compareTo(BigDecimal.ZERO) > 0);
        boolean allComplete = items.stream().allMatch(i ->
                i.getReceivedQuantity().compareTo(i.getQuantity()) >= 0);

        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));

        if (allComplete) {
            po.setStatus(STATUS_RECEIVED);
        } else if (anyReceived) {
            po.setStatus(STATUS_PARTIALLY_RECEIVED);
        }
        purchaseOrderRepository.save(po);
    }

    private void recalculateTotals(Long purchaseOrderId) {
        PurchaseOrder po = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(purchaseOrderId);
        BigDecimal subtotal = items.stream()
                .map(PurchaseOrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal discount = po.getDiscountAmount() == null ? BigDecimal.ZERO : po.getDiscountAmount();
        BigDecimal tax = po.getTaxAmount() == null ? BigDecimal.ZERO : po.getTaxAmount();
        discount = discount.setScale(2, RoundingMode.HALF_UP);
        tax = tax.setScale(2, RoundingMode.HALF_UP);

        po.setSubtotal(subtotal);
        po.setTotalAmount(subtotal.subtract(discount).add(tax).setScale(2, RoundingMode.HALF_UP));
        purchaseOrderRepository.save(po);
    }

    private String generatePoNumber(Long storeId) {
        return "PO-" + storeId + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private PurchaseOrderSummaryResponse toSummary(PurchaseOrder po) {
        return new PurchaseOrderSummaryResponse(
                po.getId(),
                po.getPoNumber(),
                po.getStatus(),
                po.getSupplierId(),
                po.getBranchId(),
                po.getOrderDate(),
                po.getTotalAmount()
        );
    }

    private PurchaseOrderDetailResponse toDetail(PurchaseOrder po, List<PurchaseOrderItem> items) {
        List<PurchaseOrderItemResponse> itemResponses = items.stream()
                .map(i -> new PurchaseOrderItemResponse(
                        i.getId(),
                        i.getProductId(),
                        i.getVariantId(),
                        i.getQuantity(),
                        i.getReceivedQuantity(),
                        i.getCostPrice(),
                        i.getLineTotal()
                ))
                .toList();

        return new PurchaseOrderDetailResponse(
                po.getId(),
                po.getPoNumber(),
                po.getStatus(),
                po.getSupplierId(),
                po.getBranchId(),
                po.getOrderDate(),
                po.getExpectedDate(),
                po.getSubtotal(),
                po.getDiscountAmount(),
                po.getTaxAmount(),
                po.getTotalAmount(),
                po.getNotes(),
                itemResponses
        );
    }
}
