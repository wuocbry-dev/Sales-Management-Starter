package com.yourcompany.salesmanagement.module.product.service.impl;

import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.product.dto.request.CreateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.response.ProductResponse;
import com.yourcompany.salesmanagement.module.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductServiceImpl implements ProductService {

    private final List<ProductResponse> products = new ArrayList<>();
    private final AtomicLong sequence = new AtomicLong(0);

    @PostConstruct
    public void seedData() {
        products.add(new ProductResponse(sequence.incrementAndGet(), "SKU-001", "Áo thun basic", "Thời trang", 199000.0, 120, "ACTIVE"));
        products.add(new ProductResponse(sequence.incrementAndGet(), "SKU-002", "Giày sneaker trắng", "Giày dép", 699000.0, 45, "ACTIVE"));
        products.add(new ProductResponse(sequence.incrementAndGet(), "SKU-003", "Bình nước thể thao", "Phụ kiện", 149000.0, 78, "ACTIVE"));
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return products;
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return products.stream()
                .filter(product -> product.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        ProductResponse product = new ProductResponse(
                sequence.incrementAndGet(),
                request.code(),
                request.name(),
                request.category(),
                request.price(),
                request.stock(),
                "ACTIVE"
        );
        products.add(product);
        return product;
    }
}
