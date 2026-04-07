package com.yourcompany.salesmanagement.module.product.service.impl;

import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.category.repository.CategoryRepository;
import com.yourcompany.salesmanagement.module.inventory.entity.Inventory;
import com.yourcompany.salesmanagement.module.inventory.repository.InventoryRepository;
import com.yourcompany.salesmanagement.module.product.dto.request.CreateProductRequest;
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
    public List<ProductResponse> getAllProducts() {
        UserPrincipal principal = SecurityUtils.requirePrincipal();
        Long storeId = SecurityUtils.requireStoreId(principal);
        Long branchId = principal.branchId();

        return productRepository.findAllByStoreId(storeId).stream()
                .map(p -> toResponse(p, storeId, branchId))
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {
        UserPrincipal principal = SecurityUtils.requirePrincipal();
        Long storeId = SecurityUtils.requireStoreId(principal);
        Long branchId = principal.branchId();

        var product = productRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));
        return toResponse(product, storeId, branchId);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        UserPrincipal principal = SecurityUtils.requirePrincipal();
        Long storeId = SecurityUtils.requireStoreId(principal);
        Long branchId = SecurityUtils.requireBranchId(principal);

        if (productRepository.existsByStoreIdAndSku(storeId, request.code())) {
            throw new BusinessException("SKU already exists", HttpStatus.CONFLICT);
        }

        Long categoryId = categoryRepository.findFirstByStoreIdAndNameIgnoreCase(storeId, request.category())
                .map(c -> c.getId())
                .orElse(null);

        Product product = new Product();
        product.setStoreId(storeId);
        product.setCategoryId(categoryId);
        product.setSku(request.code());
        product.setName(request.name());
        product.setSellingPrice(BigDecimal.valueOf(request.price()));
        product.setTrackInventory(true);
        product.setStatus("ACTIVE");
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

        inventory.setQuantity(BigDecimal.valueOf(request.stock()));
        inventoryRepository.save(inventory);

        return toResponse(product, storeId, branchId);
    }

    private ProductResponse toResponse(Product p, Long storeId, Long branchId) {
        String categoryName = (p.getCategoryId() == null) ? "-" :
                categoryRepository.findById(p.getCategoryId()).map(c -> c.getName()).orElse("-");

        int stock = 0;
        if (branchId != null) {
            var qty = inventoryRepository.getAvailableQuantityByProduct(storeId, branchId, p.getId());
            stock = qty == null ? 0 : qty.intValue();
        }

        return new ProductResponse(
                p.getId(),
                p.getSku(),
                p.getName(),
                categoryName,
                p.getSellingPrice() == null ? 0.0 : p.getSellingPrice().doubleValue(),
                stock,
                p.getStatus()
        );
    }
}
