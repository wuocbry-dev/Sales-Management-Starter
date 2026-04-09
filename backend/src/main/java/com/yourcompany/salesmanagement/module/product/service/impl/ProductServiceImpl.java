package com.yourcompany.salesmanagement.module.product.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.common.audit.AuditLoggable;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.category.repository.CategoryRepository;
import com.yourcompany.salesmanagement.module.inventory.entity.Inventory;
import com.yourcompany.salesmanagement.module.inventory.repository.InventoryRepository;
import com.yourcompany.salesmanagement.module.product.dto.request.CreateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.request.UpdateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.response.ProductResponse;
import com.yourcompany.salesmanagement.module.product.entity.Product;
import com.yourcompany.salesmanagement.module.product.repository.ProductRepository;
import com.yourcompany.salesmanagement.module.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<ProductResponse> getAllProducts(String keyword, Long categoryId, String status) {
        UserPrincipal principal = SecurityUtils.requirePrincipal();
        Long storeId = SecurityUtils.requireStoreId();
        Long branchId = principal.branchId();

        return productRepository.search(storeId, keyword, categoryId, status).stream()
                .map(p -> toResponse(p, storeId, branchId))
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        UserPrincipal principal = SecurityUtils.requirePrincipal();
        Long storeId = SecurityUtils.requireStoreId();
        Long branchId = principal.branchId();

        var product = productRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));
        return toResponse(product, storeId, branchId);
    }

    @Override
    @Transactional
    @AuditLoggable(module = "product", action = "CREATE", entityType = "Product")
    public ProductResponse createProduct(CreateProductRequest request) {
        SecurityUtils.requirePrincipal();
        Long storeId = SecurityUtils.requireStoreId();
        Long branchId = SecurityUtils.requireBranchId();

        if (productRepository.existsByStoreIdAndSku(storeId, request.sku())) {
            throw new BusinessException("SKU already exists", HttpStatus.CONFLICT);
        }

        Product product = new Product();
        product.setStoreId(storeId);
        product.setCategoryId(request.categoryId());
        product.setSupplierId(request.supplierId());
        product.setSku(request.sku());
        product.setName(request.name());
        product.setSellingPrice(BigDecimal.valueOf(request.sellingPrice()));
        product.setTrackInventory(request.trackInventory() == null ? true : request.trackInventory());
        product.setStatus(request.status() == null || request.status().isBlank() ? "ACTIVE" : request.status().trim());
        product = productRepository.save(product);
        final Long productId = product.getId();

        var inventory = inventoryRepository.findFirstByStoreIdAndBranchIdAndProductIdAndVariantIdIsNull(storeId, branchId, product.getId())
                .orElseGet(() -> {
                    Inventory i = new Inventory();
                    i.setStoreId(storeId);
                    i.setBranchId(branchId);
                    i.setProductId(productId);
                    i.setVariantId(null);
                    i.setReservedQuantity(BigDecimal.ZERO);
                    i.setQuantity(BigDecimal.ZERO);
                    i.setMinQuantity(BigDecimal.ZERO);
                    return i;
                });

        // For MVP: create product does not force inventory quantity. Inventory is managed by PO/SO flows.
        inventoryRepository.save(inventory);

        return toResponse(product, storeId, branchId);
    }

    @Override
    @Transactional
    @AuditLoggable(module = "product", action = "UPDATE", entityType = "Product")
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        SecurityUtils.requirePrincipal();
        Long storeId = SecurityUtils.requireStoreId();
        Long branchId = SecurityUtils.requireBranchId();

        Product product = productRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        String newSku = request.sku().trim();
        if (!newSku.equalsIgnoreCase(product.getSku()) && productRepository.existsByStoreIdAndSku(storeId, newSku)) {
            throw new BusinessException("SKU already exists", HttpStatus.CONFLICT);
        }

        product.setSku(newSku);
        product.setName(request.name().trim());
        product.setCategoryId(request.categoryId());
        product.setSupplierId(request.supplierId());
        product.setSellingPrice(BigDecimal.valueOf(request.sellingPrice()));
        if (request.trackInventory() != null) product.setTrackInventory(request.trackInventory());
        if (request.status() != null && !request.status().isBlank()) product.setStatus(request.status().trim());

        product = productRepository.save(product);
        return toResponse(product, storeId, branchId);
    }

    @Override
    @Transactional
    @AuditLoggable(module = "product", action = "DISABLE", entityType = "Product")
    public void deleteProduct(Long id) {
        SecurityUtils.requirePrincipal();
        Long storeId = SecurityUtils.requireStoreId();

        Product product = productRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        // Soft-delete to avoid FK restrictions (sales/purchase items may reference products).
        product.setStatus("INACTIVE");
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product p, Long storeId, Long branchId) {
        return new ProductResponse(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getCategoryId(),
                (p.getCategoryId() == null) ? "-" : categoryRepository.findById(p.getCategoryId()).map(c -> c.getName()).orElse("-"),
                p.getSellingPrice() == null ? 0.0 : p.getSellingPrice().doubleValue(),
                p.getTrackInventory(),
                p.getStatus()
        );
    }
}
