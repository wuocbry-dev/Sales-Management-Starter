package com.yourcompany.salesmanagement.module.salesorder.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.common.audit.AuditLoggable;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import com.yourcompany.salesmanagement.module.branch.entity.Branch;
import com.yourcompany.salesmanagement.module.customer.dto.response.CustomerResponse;
import com.yourcompany.salesmanagement.module.customer.service.CustomerService;
import com.yourcompany.salesmanagement.module.employee.entity.Employee;
import com.yourcompany.salesmanagement.module.employee.repository.EmployeeRepository;
import com.yourcompany.salesmanagement.module.inventory.entity.Inventory;
import com.yourcompany.salesmanagement.module.inventory.repository.InventoryRepository;
import com.yourcompany.salesmanagement.module.loyalty.service.LoyaltyService;
import com.yourcompany.salesmanagement.module.payment.entity.Payment;
import com.yourcompany.salesmanagement.module.payment.repository.PaymentRepository;
import com.yourcompany.salesmanagement.module.promotion.entity.Promotion;
import com.yourcompany.salesmanagement.module.promotion.repository.PromotionRepository;
import com.yourcompany.salesmanagement.module.product.entity.Product;
import com.yourcompany.salesmanagement.module.product.repository.ProductRepository;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.ApplyPromotionRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.ApplyVoucherRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.CreateSalesOrderItemRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.request.CreateSalesOrderRequest;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.InvoicePartyResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.InvoicePaymentResponse;
import com.yourcompany.salesmanagement.module.salesorder.dto.response.InvoiceResponse;
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
import com.yourcompany.salesmanagement.module.voucher.entity.Voucher;
import com.yourcompany.salesmanagement.module.voucher.repository.VoucherRepository;
import com.yourcompany.salesmanagement.module.store.entity.Store;
import com.yourcompany.salesmanagement.module.store.repository.StoreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Locale;

@Service
public class SalesOrderServiceImpl implements SalesOrderService {

    private static final String SOURCE_POS = "POS";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_HOLD = "HOLD";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String DISCOUNT_SOURCE_VOUCHER = "VOUCHER";
    private static final String DISCOUNT_SOURCE_PROMOTION = "PROMOTION";

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final CustomerService customerService;
    private final LoyaltyService loyaltyService;
    private final VoucherRepository voucherRepository;
    private final PromotionRepository promotionRepository;
    private final PaymentRepository paymentRepository;
    private final StoreRepository storeRepository;
    private final EmployeeRepository employeeRepository;

    public SalesOrderServiceImpl(
            SalesOrderRepository salesOrderRepository,
            SalesOrderItemRepository salesOrderItemRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            InventoryRepository inventoryRepository,
            BranchRepository branchRepository,
            CustomerService customerService,
            LoyaltyService loyaltyService,
            VoucherRepository voucherRepository,
            PromotionRepository promotionRepository,
            PaymentRepository paymentRepository,
            StoreRepository storeRepository,
            EmployeeRepository employeeRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderItemRepository = salesOrderItemRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.inventoryRepository = inventoryRepository;
        this.branchRepository = branchRepository;
        this.customerService = customerService;
        this.loyaltyService = loyaltyService;
        this.voucherRepository = voucherRepository;
        this.promotionRepository = promotionRepository;
        this.paymentRepository = paymentRepository;
        this.storeRepository = storeRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    @AuditLoggable(module = "salesorder", action = "CREATE_LEGACY", entityType = "SalesOrder")
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

        // Legacy/MVP POS: validate stock and deduct immediately on create
        SalesOrder pending = salesOrderRepository.findByIdAndStoreId(so.getId(), storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        List<SalesOrderItem> savedItems = salesOrderItemRepository.findAllBySalesOrderId(so.getId());
        validateAndDeductInventory(storeId, pending.getBranchId(), savedItems);

        pending.setStatus(STATUS_COMPLETED);
        salesOrderRepository.save(pending);

        // Customer & loyalty (MVP): only when customerId is present
        if (pending.getCustomerId() != null) {
            int earnedPoints = calculateEarnedPoints(pending.getTotalAmount());
            customerService.applyPurchase(pending.getCustomerId(), pending.getTotalAmount(), earnedPoints, pending.getOrderedAt());
            loyaltyService.earnPointsForSalesOrder(pending.getCustomerId(), pending.getId(), earnedPoints,
                    "Earn points from order " + pending.getOrderNumber());
        }

        SalesOrder refreshed = salesOrderRepository.findByIdAndStoreId(pending.getId(), storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        List<SalesOrderItem> finalItems = salesOrderItemRepository.findAllBySalesOrderId(pending.getId());
        return toDetail(refreshed, finalItems);
    }

    @Override
    @Transactional
    @AuditLoggable(module = "salesorder", action = "CREATE_HOLD", entityType = "SalesOrder")
    public SalesOrderDetailResponse createV2Hold(CreateSalesOrderRequest request) {
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
        so.setStatus(STATUS_HOLD);
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

        // v2: reserve stock (safe MVP). No deduction until COMPLETE.
        reserveInventory(storeId, so.getBranchId(), items);

        SalesOrder refreshed = salesOrderRepository.findByIdAndStoreId(so.getId(), storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        List<SalesOrderItem> finalItems = salesOrderItemRepository.findAllBySalesOrderId(so.getId());
        return toDetail(refreshed, finalItems);
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
    public InvoiceResponse getInvoice(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        SalesOrder so = salesOrderRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        List<SalesOrderItem> items = salesOrderItemRepository.findAllBySalesOrderId(so.getId());
        SalesOrderDetailResponse order = toDetail(so, items);

        // Store
        Store store = storeRepository.findById(storeId).orElse(null);
        InvoicePartyResponse storeParty = store == null
                ? new InvoicePartyResponse(storeId, null, null, null)
                : new InvoicePartyResponse(store.getId(), store.getCode(), store.getName(), null);

        // Branch
        Branch b = branchRepository.findByIdAndStoreId(so.getBranchId(), storeId).orElse(null);
        InvoicePartyResponse branchParty = b == null
                ? new InvoicePartyResponse(so.getBranchId(), null, null, null)
                : new InvoicePartyResponse(b.getId(), b.getCode(), b.getName(), null);

        // Customer
        InvoicePartyResponse customerParty = null;
        if (so.getCustomerId() != null) {
            CustomerResponse c = customerService.getById(so.getCustomerId());
            customerParty = new InvoicePartyResponse(c.id(), c.customerCode(), c.fullName(), c.phone());
        }

        // Cashier / sold by (userId)
        InvoicePartyResponse cashierParty = null;
        if (so.getSoldBy() != null) {
            Employee e = employeeRepository.findFirstByUserId(so.getSoldBy()).orElse(null);
            cashierParty = e == null
                    ? new InvoicePartyResponse(so.getSoldBy(), null, null, null)
                    : new InvoicePartyResponse(e.getId(), e.getEmployeeCode(), e.getFullName(), null);
        }

        // Payments
        List<InvoicePaymentResponse> payments = paymentRepository
                .findAllByStoreIdAndSalesOrderIdOrderByIdDesc(storeId, so.getId()).stream()
                .map(this::toInvoicePayment)
                .toList();

        return new InvoiceResponse(
                order,
                storeParty,
                branchParty,
                customerParty,
                cashierParty,
                payments,
                LocalDateTime.now()
        );
    }

    private InvoicePaymentResponse toInvoicePayment(Payment p) {
        return new InvoicePaymentResponse(
                p.getId(),
                p.getPaymentCode(),
                p.getPaymentMethod(),
                p.getAmount(),
                p.getPaidAt(),
                p.getTransactionRef(),
                p.getNotes()
        );
    }

    @Override
    @Transactional
    @AuditLoggable(module = "salesorder", action = "COMPLETE", entityType = "SalesOrder")
    public SalesOrderDetailResponse complete(Long id) {
        Long storeId = SecurityUtils.requireStoreId();

        SalesOrder so = salesOrderRepository.findForUpdateByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        if (STATUS_COMPLETED.equals(so.getStatus())) {
            return getById(id);
        }
        if (!STATUS_PENDING.equals(so.getStatus()) && !STATUS_HOLD.equals(so.getStatus())) {
            throw new BusinessException("Only PENDING or HOLD orders can be completed", HttpStatus.BAD_REQUEST);
        }

        List<SalesOrderItem> items = salesOrderItemRepository.findAllBySalesOrderId(so.getId());
        if (items.isEmpty()) {
            throw new BusinessException("Sales order has no items", HttpStatus.BAD_REQUEST);
        }

        Long branchId = so.getBranchId();
        if (STATUS_HOLD.equals(so.getStatus())) {
            // Consume reserved stock: quantity -= qty and reserved -= qty
            consumeReservedAndDeductInventory(storeId, branchId, items);
        } else {
            // PENDING without reservation: legacy behavior
            validateAndDeductInventory(storeId, branchId, items);
        }

        so.setStatus(STATUS_COMPLETED);
        salesOrderRepository.save(so);

        // MVP: mark voucher usage on completion (avoids counting unused holds)
        if (so.getAppliedVoucherId() != null && so.getAppliedVoucherCode() != null && !so.getAppliedVoucherCode().isBlank()) {
            String code = so.getAppliedVoucherCode().trim().toUpperCase(Locale.ROOT);
            Voucher v = voucherRepository.findForUpdateByStoreIdAndCode(storeId, code).orElse(null);
            if (v != null) {
                int used = v.getUsedCount() == null ? 0 : v.getUsedCount();
                v.setUsedCount(used + 1);
                voucherRepository.save(v);
            }
        }

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

    @Override
    @Transactional
    @AuditLoggable(module = "salesorder", action = "HOLD", entityType = "SalesOrder")
    public SalesOrderDetailResponse hold(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        SalesOrder so = salesOrderRepository.findForUpdateByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        if (STATUS_HOLD.equals(so.getStatus())) {
            return getById(id);
        }
        if (!STATUS_PENDING.equals(so.getStatus())) {
            throw new BusinessException("Only PENDING orders can be held", HttpStatus.BAD_REQUEST);
        }

        List<SalesOrderItem> items = salesOrderItemRepository.findAllBySalesOrderId(so.getId());
        if (items.isEmpty()) {
            throw new BusinessException("Sales order has no items", HttpStatus.BAD_REQUEST);
        }

        reserveInventory(storeId, so.getBranchId(), items);
        so.setStatus(STATUS_HOLD);
        salesOrderRepository.save(so);

        SalesOrder refreshed = salesOrderRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));
        return toDetail(refreshed, items);
    }

    @Override
    @Transactional
    public SalesOrderDetailResponse applyVoucher(Long id, ApplyVoucherRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        SalesOrder so = salesOrderRepository.findForUpdateByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        assertDiscountEditable(so);

        String code = request.code().trim().toUpperCase(Locale.ROOT);
        Voucher v = voucherRepository.findByStoreIdAndCode(storeId, code)
                .orElseThrow(() -> new BusinessException("Voucher not found", HttpStatus.NOT_FOUND));
        validateVoucher(v, so.getSubtotal());

        BigDecimal discount = calculateDiscount(v.getDiscountType(), v.getDiscountValue(), v.getMaxDiscountAmount(), so.getSubtotal());

        so.setDiscountSource(DISCOUNT_SOURCE_VOUCHER);
        so.setAppliedVoucherId(v.getId());
        so.setAppliedVoucherCode(v.getCode());
        so.setAppliedPromotionId(null);
        so.setAppliedPromotionCode(null);
        so.setDiscountAmount(discount);
        salesOrderRepository.save(so);
        recalculateTotals(so.getId());

        return getById(id);
    }

    @Override
    @Transactional
    public SalesOrderDetailResponse applyPromotion(Long id, ApplyPromotionRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        SalesOrder so = salesOrderRepository.findForUpdateByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Sales order not found", HttpStatus.NOT_FOUND));

        assertDiscountEditable(so);

        Promotion p = promotionRepository.findByIdAndStoreId(request.promotionId(), storeId)
                .orElseThrow(() -> new BusinessException("Promotion not found", HttpStatus.NOT_FOUND));
        validatePromotion(p, so.getSubtotal());

        BigDecimal discount = calculateDiscount(p.getValueType(), p.getValueAmount(), p.getMaxDiscountAmount(), so.getSubtotal());

        so.setDiscountSource(DISCOUNT_SOURCE_PROMOTION);
        so.setAppliedPromotionId(p.getId());
        so.setAppliedPromotionCode(p.getCode());
        so.setAppliedVoucherId(null);
        so.setAppliedVoucherCode(null);
        so.setDiscountAmount(discount);
        salesOrderRepository.save(so);
        recalculateTotals(so.getId());

        return getById(id);
    }

    private int calculateEarnedPoints(BigDecimal totalAmount) {
        if (totalAmount == null) return 0;
        // MVP rule: 1 point per 1,000 VND, floor
        BigDecimal points = totalAmount.setScale(0, RoundingMode.DOWN).divide(BigDecimal.valueOf(1000), RoundingMode.DOWN);
        return Math.max(0, points.intValue());
    }

    private void validateAndDeductInventory(Long storeId, Long branchId, List<SalesOrderItem> items) {
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
    }

    private void reserveInventory(Long storeId, Long branchId, List<SalesOrderItem> items) {
        Map<InventoryKey, BigDecimal> required = new HashMap<>();
        for (SalesOrderItem item : items) {
            InventoryKey key = new InventoryKey(item.getProductId(), item.getVariantId());
            required.merge(key, item.getQuantity(), BigDecimal::add);
        }

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
                        "Insufficient stock to reserve for product " + productId + (variantId == null ? "" : ("/variant " + variantId))
                                + ". Available=" + available + ", required=" + qty,
                        HttpStatus.BAD_REQUEST
                );
            }

            inv.setReservedQuantity(inv.getReservedQuantity().add(qty).setScale(2, RoundingMode.HALF_UP));
            inventoryRepository.save(inv);
        }
    }

    private void consumeReservedAndDeductInventory(Long storeId, Long branchId, List<SalesOrderItem> items) {
        Map<InventoryKey, BigDecimal> required = new HashMap<>();
        for (SalesOrderItem item : items) {
            InventoryKey key = new InventoryKey(item.getProductId(), item.getVariantId());
            required.merge(key, item.getQuantity(), BigDecimal::add);
        }

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
            BigDecimal reserved = inv.getReservedQuantity() == null ? zeroMoney() : inv.getReservedQuantity();
            if (reserved.compareTo(qty) < 0) {
                throw new BusinessException(
                        "Reserved stock is insufficient for product " + productId + (variantId == null ? "" : ("/variant " + variantId))
                                + ". Reserved=" + reserved + ", required=" + qty,
                        HttpStatus.BAD_REQUEST
                );
            }

            inv.setReservedQuantity(reserved.subtract(qty).setScale(2, RoundingMode.HALF_UP));
            inv.setQuantity(inv.getQuantity().subtract(qty).setScale(2, RoundingMode.HALF_UP));
            inventoryRepository.save(inv);
        }
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
                so.getDiscountSource(),
                so.getAppliedVoucherCode(),
                so.getAppliedPromotionId(),
                so.getAppliedPromotionCode(),
                so.getNotes(),
                so.getSoldBy(),
                so.getOrderedAt(),
                itemResponses
        );
    }

    private record InventoryKey(Long productId, Long variantId) {}

    private void assertDiscountEditable(SalesOrder so) {
        if (STATUS_COMPLETED.equalsIgnoreCase(so.getStatus())) {
            throw new BusinessException("Cannot apply discount to a completed order", HttpStatus.BAD_REQUEST);
        }
        if (so.getPaidAmount() != null && so.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Cannot apply discount after payments have been recorded", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateVoucher(Voucher v, BigDecimal subtotal) {
        if (!"ACTIVE".equalsIgnoreCase(v.getStatus())) {
            throw new BusinessException("Voucher is not active", HttpStatus.BAD_REQUEST);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(v.getStartAt()) || now.isAfter(v.getEndAt())) {
            throw new BusinessException("Voucher is not in valid time window", HttpStatus.BAD_REQUEST);
        }
        BigDecimal minOrder = money(v.getMinOrderAmount());
        if (money(subtotal).compareTo(minOrder) < 0) {
            throw new BusinessException("Order amount is below min order amount", HttpStatus.BAD_REQUEST);
        }
        int used = v.getUsedCount() == null ? 0 : v.getUsedCount();
        int limit = v.getUsageLimit() == null ? 0 : v.getUsageLimit();
        if (limit > 0 && used >= limit) {
            throw new BusinessException("Voucher usage limit exceeded", HttpStatus.BAD_REQUEST);
        }
    }

    private void validatePromotion(Promotion p, BigDecimal subtotal) {
        if (!"ACTIVE".equalsIgnoreCase(p.getStatus())) {
            throw new BusinessException("Promotion is not active", HttpStatus.BAD_REQUEST);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(p.getStartAt()) || now.isAfter(p.getEndAt())) {
            throw new BusinessException("Promotion is not in valid time window", HttpStatus.BAD_REQUEST);
        }
        BigDecimal minOrder = money(p.getMinOrderAmount());
        if (money(subtotal).compareTo(minOrder) < 0) {
            throw new BusinessException("Order amount is below min order amount", HttpStatus.BAD_REQUEST);
        }
    }

    private BigDecimal calculateDiscount(String type, BigDecimal value, BigDecimal maxDiscountAmount, BigDecimal subtotal) {
        String t = type == null ? "" : type.trim().toUpperCase(Locale.ROOT);
        BigDecimal base = money(subtotal);
        BigDecimal discount;
        if ("PERCENT".equals(t) || "PERCENTAGE".equals(t)) {
            BigDecimal pct = money(value);
            discount = base.multiply(pct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            // AMOUNT/FIXED
            discount = money(value);
        }

        if (maxDiscountAmount != null) {
            BigDecimal cap = money(maxDiscountAmount);
            if (discount.compareTo(cap) > 0) discount = cap;
        }
        if (discount.compareTo(base) > 0) discount = base;
        if (discount.compareTo(BigDecimal.ZERO) < 0) discount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal money(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}

