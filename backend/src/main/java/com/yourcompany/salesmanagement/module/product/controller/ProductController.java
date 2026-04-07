package com.yourcompany.salesmanagement.module.product.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.product.dto.request.CreateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.response.ProductResponse;
import com.yourcompany.salesmanagement.module.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public BaseResponse<List<ProductResponse>> getAllProducts() {
        return BaseResponse.ok("Products fetched successfully", productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public BaseResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return BaseResponse.ok("Product fetched successfully", productService.getProductById(id));
    }

    @PostMapping
    public BaseResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return BaseResponse.ok("Product created successfully", productService.createProduct(request));
    }
}
