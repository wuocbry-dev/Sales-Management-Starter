package com.yourcompany.salesmanagement.module.inventory.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import com.yourcompany.salesmanagement.module.inventory.dto.request.InventoryAdjustRequest;
import com.yourcompany.salesmanagement.module.inventory.dto.response.InventoryResponse;
import com.yourcompany.salesmanagement.module.inventory.entity.Inventory;
import com.yourcompany.salesmanagement.module.inventory.entity.InventoryMovement;
import com.yourcompany.salesmanagement.module.inventory.repository.InventoryMovementRepository;
import com.yourcompany.salesmanagement.module.inventory.repository.InventoryRepository;
import com.yourcompany.salesmanagement.module.inventory.service.InventoryService;
import com.yourcompany.salesmanagement.module.product.repository.ProductRepository;
import com.yourcompany.salesmanagement.module.variant.repository.ProductVariantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository inventoryMovementRepository,
            BranchRepository branchRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public List<InventoryResponse> listByBranch(Long branchId) {
        Long storeId = SecurityUtils.requireStoreId();
        branchRepository.findByIdAndStoreId(branchId, storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));

        return inventoryRepository.findAllByStoreIdAndBranchIdOrderByIdDesc(storeId, branchId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public InventoryResponse getById(Long id) {
        Long storeId = SecurityUtils.requireStoreId();
        Inventory inv = inventoryRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Inventory not found", HttpStatus.NOT_FOUND));
        return toResponse(inv);
    }

    @Override
    @Transactional
    public InventoryResponse adjust(InventoryAdjustRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Long userId = SecurityUtils.requirePrincipal().userId();
        Long branchId = request.branchId();
        branchRepository.findByIdAndStoreId(branchId, storeId)
                .orElseThrow(() -> new BusinessException("Branch not found", HttpStatus.NOT_FOUND));

        productRepository.findByIdAndStoreId(request.productId(), storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        Long variantId = request.variantId();
        if (variantId != null) {
            productVariantRepository.findByIdAndProductId(variantId, request.productId())
                    .orElseThrow(() -> new BusinessException("Variant not found for this product", HttpStatus.NOT_FOUND));
        }

        BigDecimal delta = request.deltaQuantity().setScale(2, RoundingMode.HALF_UP);
        if (delta.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException("Delta quantity must not be 0", HttpStatus.BAD_REQUEST);
        }

        Inventory inv;
        if (variantId == null) {
            inv = inventoryRepository
                    .findFirstByStoreIdAndBranchIdAndProductIdAndVariantIdIsNull(storeId, branchId, request.productId())
                    .orElseGet(() -> newInventoryRow(storeId, branchId, request.productId(), null));
        } else {
            inv = inventoryRepository
                    .findByStoreIdAndBranchIdAndProductIdAndVariantId(storeId, branchId, request.productId(), variantId)
                    .orElseGet(() -> newInventoryRow(storeId, branchId, request.productId(), variantId));
        }

        BigDecimal current = inv.getQuantity() == null ? BigDecimal.ZERO : inv.getQuantity();
        BigDecimal next = current.add(delta).setScale(2, RoundingMode.HALF_UP);
        if (next.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Inventory cannot be negative", HttpStatus.BAD_REQUEST);
        }

        inv.setQuantity(next);
        inv = inventoryRepository.save(inv);
        logMovement(storeId, branchId, request.productId(), variantId,
                "ADJUST", "inventory_adjust", inv.getId(),
                delta, current, next, request.reason(), userId);
        return toResponse(inv);
    }

    private void logMovement(
            Long storeId,
            Long branchId,
            Long productId,
            Long variantId,
            String movementType,
            String referenceType,
            Long referenceId,
            BigDecimal delta,
            BigDecimal beforeQty,
            BigDecimal afterQty,
            String note,
            Long createdBy) {
        InventoryMovement m = new InventoryMovement();
        m.setStoreId(storeId);
        m.setBranchId(branchId);
        m.setProductId(productId);
        m.setVariantId(variantId);
        m.setMovementType(movementType);
        m.setReferenceType(referenceType);
        m.setReferenceId(referenceId);
        m.setDeltaQuantity(delta);
        m.setBeforeQuantity(beforeQty);
        m.setAfterQuantity(afterQty);
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

    private InventoryResponse toResponse(Inventory inv) {
        return new InventoryResponse(
                inv.getId(),
                inv.getStoreId(),
                inv.getBranchId(),
                inv.getProductId(),
                inv.getVariantId(),
                inv.getQuantity(),
                inv.getReservedQuantity(),
                inv.getMinQuantity(),
                inv.getMaxQuantity()
        );
    }
}

