package com.yourcompany.salesmanagement.module.product.service;

import com.yourcompany.salesmanagement.module.product.dto.request.CreateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.request.UpdateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts(String keyword, Long categoryId, String status);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse updateProduct(Long id, UpdateProductRequest request);
    void deleteProduct(Long id);
}
