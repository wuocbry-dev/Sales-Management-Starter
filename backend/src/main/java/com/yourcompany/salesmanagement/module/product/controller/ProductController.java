package com.yourcompany.salesmanagement.module.product.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.product.dto.request.CreateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.request.UpdateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.response.ProductResponse;
import com.yourcompany.salesmanagement.module.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/products", "/api/products"})
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public BaseResponse<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status
    ) {
        return BaseResponse.ok("Products fetched successfully", productService.getAllProducts(keyword, categoryId, status));
    }

    @GetMapping("/{id}")
    public BaseResponse<ProductResponse> getProductById(@PathVariable Long id) {
        return BaseResponse.ok("Product fetched successfully", productService.getProductById(id));
    }

    @PostMapping
    public BaseResponse<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return BaseResponse.ok("Product created successfully", productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public BaseResponse<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return BaseResponse.ok("Product updated successfully", productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Object> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return BaseResponse.ok("Product deleted successfully", null);
    }
}
