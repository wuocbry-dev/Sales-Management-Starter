package com.yourcompany.salesmanagement.module.salesorder.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import com.yourcompany.salesmanagement.module.customer.service.CustomerService;
import com.yourcompany.salesmanagement.module.inventory.entity.Inventory;
import com.yourcompany.salesmanagement.module.inventory.repository.InventoryRepository;
import com.yourcompany.salesmanagement.module.loyalty.service.LoyaltyService;
import com.yourcompany.salesmanagement.module.product.entity.Product;
import com.yourcompany.salesmanagement.module.product.repository.ProductRepository;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.CreateSalesOrderItemRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.CreateSalesOrderRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderDetailResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderItemResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.SalesOrderSummaryResponse;
import com.yourcompany.salesmanagement.module.salesorder.entity.SalesOrder;
import com.yourcompany.salesmanagement.module.salesorder.entity.SalesOrderItem;
import com.yourcompany.salesmanagement.module.salesorder.repository.SalesOrderItemRepository;
import com.yourcompany.salesmanagement.module.salesorder.repository.SalesOrderRepository;
import com.yourcompany.salesmanagement.module.salesorder.service.SalesOrderService;
import com.yourcompany.salesmanagement.module.variant.entity.ProductVariant;
import com.yourcompany.salesmanagement.module.variant.repository.ProductVariantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private static final String SOURCE_POS = "POS";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_COMPLETED = "COMPLETED";

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final CustomerService customerService;
    private final LoyaltyService loyaltyService;

    public SalesOrderServiceImpl(
            SalesOrderRepository salesOrderRepository,
            SalesOrderItemRepository salesOrderItemRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            InventoryRepository inventoryRepository,
            BranchRepository branchRepository,
            CustomerService customerService,
            LoyaltyService loyaltyService) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderItemRepository = salesOrderItemRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.inventoryRepository = inventoryRepository;
        this.branchRepository = branchRepository;
        this.customerService = customerService;
        this.loyaltyService = loyaltyService;
    }

    @Override
    @Transactional
    public SalesOrderDetailResponse create(CreateSalesOrderRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        UserPrincipal principal = SecurityUtils.requirePrincipal();

        branchRepository.findByIdAndStoreId(request.branchId(), storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));

        SalesOrder so = new SalesOrder();
        so.setStoreId(storeId);
        so.setBranchId(request.branchId());
        so.setCustomerId(request.customerId());
        so.setOrderNumber(generateOrderNumber(storeId));
        so.setOrderSource(SOURCE_POS);
        so.setStatus(STATUS_PENDING);
        so.setSubtotal(zeroMoney());
        so.setDiscountAmount(zeroMoney());
        so.setTaxAmount(zeroMoney());
        so.setShippingFee(zeroMoney());
        so.setTotalAmount(zeroMoney());
        so.setPaidAmount(zeroMoney());
        so.setNotes(request.notes());
        so.setSoldBy(principal.userId());
        so.setOrderedAt(LocalDateTime.now());
        so = salesOrderRepository.save(so);

        List<SalesOrderItem> items = buildItems(storeId, so.getId(), request.items());
        salesOrderItemRepository.saveAll(items);

        recalculateTotals(so.getId());
        SalesOrder refreshed = salesOrderRepository.findByIdAndStoreId(so.getId(), storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        List<SalesOrderItem> savedItems = salesOrderItemRepository.findAllBySalesOrderId(so.getId());
        return toDetail(refreshed, savedItems);
    }

    @Override
    public List<SalesOrderSummaryResponse> list() {
        Long storeId = SecurityUtils.requireStoreId();
        return salesOrderRepository.findAllByStoreIdOrderByIdDesc(storeId).stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public SalesOrderDetailResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        SalesOrder so = salesOrderRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        List<SalesOrderItem> items = salesOrderItemRepository.findAllBySalesOrderId(so.getId());
        return toDetail(so, items);
    }

    @Override
    @Transactional
    public SalesOrderDetailResponse complete(Long id) {
        Long storeId = SecurityUtils.requireStoreId();

        SalesOrder so = salesOrderRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        if (STATUS_COMPLETED.equals(so.getStatus())) {
            return getById(id);
        }
        if (!STATUS_PENDING.equals(so.getStatus())) {
            throw new BusinessException("Only PENDING orders can be completed", HttpStatus.BAD_REQUEST);
        }

        List<SalesOrderItem> items = salesOrderItemRepository.findAllBySalesOrderId(so.getId());
        if (items.isEmpty()) {
            throw new BusinessException("Sales order has no items", HttpStatus.BAD_REQUEST);
        }

        Long branchId = so.getBranchId();
        Map<InventoryKey, BigDecimal> required = new HashMap<>();
        for (SalesOrderItem item : items) {
            InventoryKey key = new InventoryKey(item.getProductId(), item.getVariantId());
            required.merge(key, item.getQuantity(), BigDecimal::add);
        }

        // Validate and deduct with row locks
        for (var entry : required.entrySet()) {
            Long productId = entry.getKey().productId();
            Long variantId = entry.getKey().variantId();
            BigDecimal qty = normalizeQty(entry.getValue());

            Product p = productRepository.findByIdAndStoreId(productId, storeId)
                    .orElseThrow(() -> new BusinessException("Product not found: " + productId, HttpStatus.NOT_FOUND));
            if (Boolean.FALSE.equals(p.getTrackInventory())) {
                continue;
            }

            Inventory inv = loadInventoryForUpdate(storeId, branchId, productId, variantId);
            BigDecimal available = inv.getQuantity().subtract(inv.getReservedQuantity()).setScale(2, RoundingMode.HALF_UP);
            if (available.compareTo(qty) < 0) {
                throw new BusinessException(
                        "Insufficient stock for product " + productId + (variantId == null ? "" : ("/variant " + variantId))
                                + ". Available=" + available + ", required=" + qty,
                        HttpStatus.BAD_REQUEST
                );
            }

            inv.setQuantity(inv.getQuantity().subtract(qty).setScale(2, RoundingMode.HALF_UP));
            inventoryRepository.save(inv);
        }

        so.setStatus(STATUS_COMPLETED);
        salesOrderRepository.save(so);

        // Customer & loyalty (MVP): only when customerId is present
        if (so.getCustomerId() != null) {
            int earnedPoints = calculateEarnedPoints(so.getTotalAmount());
            customerService.applyPurchase(so.getCustomerId(), so.getTotalAmount(), earnedPoints, so.getOrderedAt());
            loyaltyService.earnPointsForSalesOrder(so.getCustomerId(), so.getId(), earnedPoints,
                    "Earn points from order " + so.getOrderNumber());
        }

        SalesOrder refreshed = salesOrderRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        List<SalesOrderItem> refreshedItems = salesOrderItemRepository.findAllBySalesOrderId(id);
        return toDetail(refreshed, refreshedItems);
    }

    private int calculateEarnedPoints(BigDecimal totalAmount) {
        if (totalAmount == null) return 0;
        // MVP rule: 1 point per 1,000 VND, floor
        BigDecimal points = totalAmount.setScale(0, RoundingMode.DOWN).divide(BigDecimal.valueOf(1000), RoundingMode.DOWN);
        return Math.max(0, points.intValue());
    }

    private List<SalesOrderItem> buildItems(Long storeId, Long salesOrderId, List<CreateSalesOrderItemRequest> reqItems) {
        List<SalesOrderItem> items = new ArrayList<>();
        for (CreateSalesOrderItemRequest r : reqItems) {
            Product p = productRepository.findByIdAndStoreId(r.productId(), storeId)
                    .orElseThrow(() -> new BusinessException("Product not found: " + r.productId(), HttpStatus.NOT_FOUND));

            ProductVariant v = null;
            if (r.variantId() != null) {
                v = productVariantRepository.findByIdAndProductId(r.variantId(), r.productId())
                        .orElseThrow(() -> new BusinessException("Variant not found for this product: " + r.variantId(), HttpStatus.NOT_FOUND));
            }

            BigDecimal qty = normalizeQty(r.quantity());
            BigDecimal unitPrice = (v != null ? v.getSellingPrice() : p.getSellingPrice());
            if (unitPrice == null) unitPrice = zeroMoney();
            unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);

            BigDecimal discount = zeroMoney(); // MVP: per-line discount not exposed yet
            BigDecimal lineTotal = qty.multiply(unitPrice).subtract(discount).setScale(2, RoundingMode.HALF_UP);

            SalesOrderItem item = new SalesOrderItem();
            item.setSalesOrderId(salesOrderId);
            item.setProductId(p.getId());
            item.setVariantId(v == null ? null : v.getId());
            item.setProductName(p.getName());
            item.setSku(v != null ? v.getSku() : p.getSku());
            item.setUnitPrice(unitPrice);
            item.setQuantity(qty);
            item.setDiscountAmount(discount);
            item.setLineTotal(lineTotal);
            items.add(item);
        }
        return items;
    }

    private void recalculateTotals(Long salesOrderId) {
        SalesOrder so = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        List<SalesOrderItem> items = salesOrderItemRepository.findAllBySalesOrderId(salesOrderId);

        BigDecimal subtotal = items.stream()
                .map(SalesOrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        so.setSubtotal(subtotal);
        so.setTotalAmount(subtotal
                .subtract(so.getDiscountAmount() == null ? zeroMoney() : so.getDiscountAmount())
                .add(so.getTaxAmount() == null ? zeroMoney() : so.getTaxAmount())
                .add(so.getShippingFee() == null ? zeroMoney() : so.getShippingFee())
                .setScale(2, RoundingMode.HALF_UP));
        salesOrderRepository.save(so);
    }

    private Inventory loadInventoryForUpdate(Long storeId, Long branchId, Long productId, Long variantId) {
        if (variantId == null) {
            return inventoryRepository.findFirstByStoreIdAndBranchIdAndProductIdAndVariantIdIsNull(storeId, branchId, productId)
                    .orElseThrow(() -> new BusinessException("No inventory row for product " + productId + " at branch " + branchId, HttpStatus.BAD_REQUEST));
        }
        return inventoryRepository.findByStoreIdAndBranchIdAndProductIdAndVariantId(storeId, branchId, productId, variantId)
                .orElseThrow(() -> new BusinessException("No inventory row for product " + productId + "/variant " + variantId + " at branch " + branchId, HttpStatus.BAD_REQUEST));
    }

    private String generateOrderNumber(Long storeId) {
        return "SO-" + storeId + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private BigDecimal normalizeQty(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal zeroMoney() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private SalesOrderSummaryResponse toSummary(SalesOrder so) {
        return new SalesOrderSummaryResponse(
                so.getId(),
                so.getOrderNumber(),
                so.getStatus(),
                so.getBranchId(),
                so.getTotalAmount(),
                so.getOrderedAt()
        );
    }

    private SalesOrderDetailResponse toDetail(SalesOrder so, List<SalesOrderItem> items) {
        List<SalesOrderItemResponse> itemResponses = items.stream()
                .map(i -> new SalesOrderItemResponse(
                        i.getId(),
                        i.getProductId(),
                        i.getVariantId(),
                        i.getProductName(),
                        i.getSku(),
                        i.getUnitPrice(),
                        i.getQuantity(),
                        i.getDiscountAmount(),
                        i.getLineTotal()
                ))
                .toList();

        return new SalesOrderDetailResponse(
                so.getId(),
                so.getOrderNumber(),
                so.getStatus(),
                so.getStoreId(),
                so.getBranchId(),
                so.getCustomerId(),
                so.getSubtotal(),
                so.getDiscountAmount(),
                so.getTaxAmount(),
                so.getShippingFee(),
                so.getTotalAmount(),
                so.getPaidAmount(),
                so.getNotes(),
                so.getSoldBy(),
                so.getOrderedAt(),
                itemResponses
        );
    }

    private record InventoryKey(Long productId, Long variantId) {}
}

