package com.yourcompany.salesmanagement.module.purchaseorder.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.common.audit.AuditLoggable;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import com.yourcompany.salesmanagement.module.inventory.entity.Inventory;
import com.yourcompany.salesmanagement.module.inventory.entity.InventoryMovement;
import com.yourcompany.salesmanagement.module.inventory.repository.InventoryMovementRepository;
import com.yourcompany.salesmanagement.module.inventory.repository.InventoryRepository;
import com.yourcompany.salesmanagement.module.product.repository.ProductRepository;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.AddPurchaseOrderItemRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CancelPurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CreatePurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CreatePurchaseReturnItemRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.CreatePurchaseReturnRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.ReceiveLineRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.request.ReceivePurchaseOrderRequest;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseOrderDetailResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseOrderItemResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseOrderSummaryResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseReturnItemResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.dto.response.PurchaseReturnResponse;
import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseOrder;
import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseOrderItem;
import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseReturn;
import com.yourcompany.salesmanagement.module.purchaseorder.entity.PurchaseReturnItem;
import com.yourcompany.salesmanagement.module.purchaseorder.repository.PurchaseOrderItemRepository;
import com.yourcompany.salesmanagement.module.purchaseorder.repository.PurchaseOrderRepository;
import com.yourcompany.salesmanagement.module.purchaseorder.repository.PurchaseReturnItemRepository;
import com.yourcompany.salesmanagement.module.purchaseorder.repository.PurchaseReturnRepository;
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
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_PARTIALLY_RECEIVED = "PARTIALLY_RECEIVED";
    private static final String STATUS_RECEIVED = "RECEIVED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private static final String RETURN_STATUS_COMPLETED = "COMPLETED";

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final PurchaseReturnRepository purchaseReturnRepository;
    private final PurchaseReturnItemRepository purchaseReturnItemRepository;

    public PurchaseOrderServiceImpl(
            PurchaseOrderRepository purchaseOrderRepository,
            PurchaseOrderItemRepository purchaseOrderItemRepository,
            SupplierRepository supplierRepository,
            BranchRepository branchRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            InventoryRepository inventoryRepository,
            InventoryMovementRepository inventoryMovementRepository,
            PurchaseReturnRepository purchaseReturnRepository,
            PurchaseReturnItemRepository purchaseReturnItemRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
        this.supplierRepository = supplierRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.purchaseReturnRepository = purchaseReturnRepository;
        this.purchaseReturnItemRepository = purchaseReturnItemRepository;
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

        List<PurchaseOrderItem> items = new ArrayList<>();
        if (request.items() != null && !request.items().isEmpty()) {
            for (AddPurchaseOrderItemRequest lineReq : request.items()) {
                items.add(addItemInternal(storeId, po, lineReq));
            }
            recalculateTotals(po.getId());
            items = purchaseOrderItemRepository.findAllByPurchaseOrderId(po.getId());

            boolean receiveNow = request.receiveNow() == null || request.receiveNow();
            if (receiveNow) {
                receive(po.getId(), null);
                PurchaseOrder refreshed = purchaseOrderRepository.findByIdAndStoreId(po.getId(), storeId)
                        .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));
                items = purchaseOrderItemRepository.findAllByPurchaseOrderId(po.getId());
                return toDetail(refreshed, items);
            }
        }

        PurchaseOrder refreshed = purchaseOrderRepository.findByIdAndStoreId(po.getId(), storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));
        return toDetail(refreshed, items);
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

        addItemInternal(storeId, po, request);

        recalculateTotals(po.getId());
        PurchaseOrder refreshed = purchaseOrderRepository.findByIdAndStoreId(po.getId(), storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));
        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(po.getId());
        return toDetail(refreshed, items);
    }

    @Override
    @Transactional
    @AuditLoggable(module = "purchaseorder", action = "CANCEL", entityType = "PurchaseOrder")
    public PurchaseOrderDetailResponse cancel(Long purchaseOrderId, CancelPurchaseOrderRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Long userId = SecurityUtils.requirePrincipal().userId();

        PurchaseOrder po = purchaseOrderRepository.findForUpdateByIdAndStoreId(purchaseOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));

        if (STATUS_CANCELLED.equalsIgnoreCase(po.getStatus())) {
            return getById(po.getId());
        }
        if (!STATUS_DRAFT.equals(po.getStatus())
                && !STATUS_PARTIALLY_RECEIVED.equals(po.getStatus())
                && !STATUS_RECEIVED.equals(po.getStatus())) {
            throw new BusinessException("Only DRAFT / PARTIALLY_RECEIVED / RECEIVED purchase orders can be cancelled", HttpStatus.BAD_REQUEST);
        }

        Long branchId = po.getBranchId();
        if (branchId == null) {
            throw new BusinessException("Purchase order has no branch", HttpStatus.BAD_REQUEST);
        }

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(po.getId());
        for (PurchaseOrderItem item : items) {
            BigDecimal received = money(item.getReceivedQuantity());
            if (received.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal alreadyReturned = money(purchaseReturnItemRepository.sumReturnedQtyByPurchaseOrderItemId(item.getId()));
            BigDecimal effective = received.subtract(alreadyReturned).setScale(2, RoundingMode.HALF_UP);
            if (effective.compareTo(BigDecimal.ZERO) <= 0) continue;

            Inventory inv = loadInventoryForUpdate(storeId, branchId, item.getProductId(), item.getVariantId());
            BigDecimal reserved = money(inv.getReservedQuantity());
            BigDecimal available = money(inv.getQuantity()).subtract(reserved).setScale(2, RoundingMode.HALF_UP);
            if (available.compareTo(effective) < 0) {
                throw new BusinessException(
                        "Cannot cancel PO because stock was already used/reserved for product " + item.getProductId()
                                + (item.getVariantId() == null ? "" : ("/variant " + item.getVariantId()))
                                + ". Available=" + available + ", required rollback=" + effective,
                        HttpStatus.CONFLICT
                );
            }

            BigDecimal before = money(inv.getQuantity());
            BigDecimal after = before.subtract(effective).setScale(2, RoundingMode.HALF_UP);
            inv.setQuantity(after);
            inventoryRepository.save(inv);

            InventoryMovement m = new InventoryMovement();
            m.setStoreId(storeId);
            m.setBranchId(branchId);
            m.setProductId(item.getProductId());
            m.setVariantId(item.getVariantId());
            m.setMovementType("PO_CANCEL");
            m.setReferenceType("purchase_order");
            m.setReferenceId(po.getId());
            m.setDeltaQuantity(effective.negate());
            m.setBeforeQuantity(before);
            m.setAfterQuantity(after);
            m.setNote("Cancel PO " + po.getPoNumber() + ": rollback received qty " + effective);
            m.setCreatedBy(userId);
            inventoryMovementRepository.save(m);

            // Prevent double rollback: keep only the qty already returned as "received baseline"
            item.setReceivedQuantity(alreadyReturned.setScale(2, RoundingMode.HALF_UP));
            purchaseOrderItemRepository.save(item);
        }

        po.setStatus(STATUS_CANCELLED);
        po.setNotes(appendNote(po.getNotes(), "Cancelled: " + safeReason(request)));
        purchaseOrderRepository.save(po);

        PurchaseOrder refreshed = purchaseOrderRepository.findByIdAndStoreId(po.getId(), storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));
        List<PurchaseOrderItem> refreshedItems = purchaseOrderItemRepository.findAllByPurchaseOrderId(po.getId());
        return toDetail(refreshed, refreshedItems);
    }

    @Override
    @Transactional
    @AuditLoggable(module = "purchaseorder", action = "RETURN", entityType = "PurchaseReturn")
    public PurchaseReturnResponse createReturn(Long purchaseOrderId, CreatePurchaseReturnRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Long userId = SecurityUtils.requirePrincipal().userId();

        PurchaseOrder po = purchaseOrderRepository.findForUpdateByIdAndStoreId(purchaseOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));

        if (STATUS_CANCELLED.equalsIgnoreCase(po.getStatus())) {
            throw new BusinessException("Cannot return items for a cancelled purchase order", HttpStatus.BAD_REQUEST);
        }
        if (!STATUS_PARTIALLY_RECEIVED.equals(po.getStatus()) && !STATUS_RECEIVED.equals(po.getStatus())) {
            throw new BusinessException("Only received purchase orders can be returned", HttpStatus.BAD_REQUEST);
        }

        Long branchId = po.getBranchId();
        if (branchId == null) throw new BusinessException("Purchase order has no branch", HttpStatus.BAD_REQUEST);
        branchRepository.findByIdAndStoreId(branchId, storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));

        PurchaseReturn pr = new PurchaseReturn();
        pr.setStoreId(storeId);
        pr.setBranchId(branchId);
        pr.setSupplierId(po.getSupplierId());
        pr.setPurchaseOrderId(po.getId());
        pr.setReturnNumber(generatePrNumber(storeId));
        pr.setStatus(RETURN_STATUS_COMPLETED);
        pr.setTotalQuantity(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        pr.setTotalAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        pr.setReason(request.reason());
        pr.setCreatedBy(userId);
        pr = purchaseReturnRepository.save(pr);

        BigDecimal totalQty = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        List<PurchaseReturnItemResponse> itemResponses = new ArrayList<>();

        for (CreatePurchaseReturnItemRequest line : request.items()) {
            PurchaseOrderItem poi = purchaseOrderItemRepository.findByIdAndPurchaseOrderId(line.purchaseOrderItemId(), po.getId())
                    .orElseThrow(() -> new BusinessException("Purchase order item not found: " + line.purchaseOrderItemId(), HttpStatus.NOT_FOUND));

            BigDecimal received = money(poi.getReceivedQuantity());
            BigDecimal alreadyReturned = money(purchaseReturnItemRepository.sumReturnedQtyByPurchaseOrderItemId(poi.getId()));
            BigDecimal remainingReturnable = received.subtract(alreadyReturned).setScale(2, RoundingMode.HALF_UP);

            BigDecimal qty = money(line.quantity());
            if (qty.compareTo(remainingReturnable) > 0) {
                throw new BusinessException(
                        "Return quantity exceeds remaining returnable for purchaseOrderItemId=" + poi.getId()
                                + ". Remaining=" + remainingReturnable + ", requested=" + qty,
                        HttpStatus.BAD_REQUEST
                );
            }

            Inventory inv = loadInventoryForUpdate(storeId, branchId, poi.getProductId(), poi.getVariantId());
            BigDecimal reserved = money(inv.getReservedQuantity());
            BigDecimal available = money(inv.getQuantity()).subtract(reserved).setScale(2, RoundingMode.HALF_UP);
            if (available.compareTo(qty) < 0) {
                throw new BusinessException(
                        "Insufficient available stock to return for product " + poi.getProductId()
                                + (poi.getVariantId() == null ? "" : ("/variant " + poi.getVariantId()))
                                + ". Available=" + available + ", required=" + qty,
                        HttpStatus.CONFLICT
                );
            }

            BigDecimal before = money(inv.getQuantity());
            BigDecimal after = before.subtract(qty).setScale(2, RoundingMode.HALF_UP);
            inv.setQuantity(after);
            inventoryRepository.save(inv);

            InventoryMovement m = new InventoryMovement();
            m.setStoreId(storeId);
            m.setBranchId(branchId);
            m.setProductId(poi.getProductId());
            m.setVariantId(poi.getVariantId());
            m.setMovementType("PO_RETURN");
            m.setReferenceType("purchase_return");
            m.setReferenceId(pr.getId());
            m.setDeltaQuantity(qty.negate());
            m.setBeforeQuantity(before);
            m.setAfterQuantity(after);
            m.setNote("Return goods for PO " + po.getPoNumber() + " item " + poi.getId());
            m.setCreatedBy(userId);
            inventoryMovementRepository.save(m);

            BigDecimal unitCost = money(poi.getCostPrice());
            BigDecimal lineTotal = qty.multiply(unitCost).setScale(2, RoundingMode.HALF_UP);

            PurchaseReturnItem pri = new PurchaseReturnItem();
            pri.setPurchaseReturnId(pr.getId());
            pri.setPurchaseOrderItemId(poi.getId());
            pri.setProductId(poi.getProductId());
            pri.setVariantId(poi.getVariantId());
            pri.setQuantity(qty);
            pri.setUnitCost(unitCost);
            pri.setLineTotal(lineTotal);
            pri = purchaseReturnItemRepository.save(pri);

            totalQty = totalQty.add(qty).setScale(2, RoundingMode.HALF_UP);
            totalAmount = totalAmount.add(lineTotal).setScale(2, RoundingMode.HALF_UP);

            itemResponses.add(new PurchaseReturnItemResponse(
                    pri.getId(),
                    pri.getPurchaseOrderItemId(),
                    pri.getProductId(),
                    pri.getVariantId(),
                    pri.getQuantity(),
                    pri.getUnitCost(),
                    pri.getLineTotal()
            ));
        }

        pr.setTotalQuantity(totalQty);
        pr.setTotalAmount(totalAmount);
        purchaseReturnRepository.save(pr);

        return new PurchaseReturnResponse(
                pr.getId(),
                pr.getPurchaseOrderId(),
                pr.getReturnNumber(),
                pr.getStatus(),
                pr.getSupplierId(),
                pr.getBranchId(),
                pr.getTotalQuantity(),
                pr.getTotalAmount(),
                pr.getReason(),
                pr.getCreatedBy(),
                pr.getCreatedAt(),
                itemResponses
        );
    }

    private PurchaseOrderItem addItemInternal(Long storeId, PurchaseOrder po, AddPurchaseOrderItemRequest request) {
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
        return purchaseOrderItemRepository.save(line);
    }

    @Override
    @Transactional
    @AuditLoggable(module = "purchaseorder", action = "RECEIVE", entityType = "PurchaseOrder")
    public PurchaseOrderDetailResponse receive(Long purchaseOrderId, ReceivePurchaseOrderRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Long userId = SecurityUtils.requirePrincipal().userId();
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

            addToInventory(storeId, branchId, item.getProductId(), item.getVariantId(), toReceive,
                    "PO_RECEIVE", "purchase_order", po.getId(),
                    "PO " + po.getPoNumber() + " receive item " + item.getId(), userId);

            item.setReceivedQuantity(item.getReceivedQuantity().add(toReceive).setScale(2, RoundingMode.HALF_UP));
            purchaseOrderItemRepository.save(item);
        }

        refreshReceivedStatus(po.getId());
        PurchaseOrder refreshed = purchaseOrderRepository.findByIdAndStoreId(purchaseOrderId, storeId)
                .orElseThrow(() -> new BusinessException("Purchase order not found", HttpStatus.NOT_FOUND));

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findAllByPurchaseOrderId(po.getId());
        return toDetail(refreshed, items);
    }

    private void addToInventory(
            Long storeId,
            Long branchId,
            Long productId,
            Long variantId,
            BigDecimal delta,
            String movementType,
            String referenceType,
            Long referenceId,
            String note,
            Long createdBy) {
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
        BigDecimal next = current.add(delta).setScale(2, RoundingMode.HALF_UP);
        inv.setQuantity(next);
        inventoryRepository.save(inv);

        InventoryMovement m = new InventoryMovement();
        m.setStoreId(storeId);
        m.setBranchId(branchId);
        m.setProductId(productId);
        m.setVariantId(variantId);
        m.setMovementType(movementType);
        m.setReferenceType(referenceType);
        m.setReferenceId(referenceId);
        m.setDeltaQuantity(delta);
        m.setBeforeQuantity(current.setScale(2, RoundingMode.HALF_UP));
        m.setAfterQuantity(next);
        m.setNote(note);
        m.setCreatedBy(createdBy);
        inventoryMovementRepository.save(m);
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

    private Inventory loadInventoryForUpdate(Long storeId, Long branchId, Long productId, Long variantId) {
        if (variantId == null) {
            return inventoryRepository.findFirstByStoreIdAndBranchIdAndProductIdAndVariantIdIsNull(storeId, branchId, productId)
                    .orElseThrow(() -> new BusinessException("No inventory row for product " + productId + " at branch " + branchId, HttpStatus.BAD_REQUEST));
        }
        return inventoryRepository.findByStoreIdAndBranchIdAndProductIdAndVariantId(storeId, branchId, productId, variantId)
                .orElseThrow(() -> new BusinessException("No inventory row for product " + productId + "/variant " + variantId + " at branch " + branchId, HttpStatus.BAD_REQUEST));
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private String safeReason(CancelPurchaseOrderRequest request) {
        if (request == null || request.reason() == null || request.reason().isBlank()) return "N/A";
        return request.reason().trim();
    }

    private String appendNote(String existing, String line) {
        String l = line == null ? "" : line.trim();
        if (l.isEmpty()) return existing;
        if (existing == null || existing.isBlank()) return l;
        return existing + "\n" + l;
    }

    private String generatePrNumber(Long storeId) {
        return "PR-" + storeId + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(Locale.ROOT);
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
