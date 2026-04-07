package com.yourcompany.salesmanagement.module.product.service;

import com.yourcompany.salesmanagement.module.product.dto.request.CreateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(CreateProductRequest request);
}
