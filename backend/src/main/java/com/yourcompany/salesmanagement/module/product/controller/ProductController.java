package com.yourcompany.salesmanagement.module.product.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.importjob.dto.response.ImportJobResponse;
import com.yourcompany.salesmanagement.module.product.dto.request.CreateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.request.UpdateProductRequest;
import com.yourcompany.salesmanagement.module.product.dto.response.ProductResponse;
import com.yourcompany.salesmanagement.module.product.service.ProductService;
import com.yourcompany.salesmanagement.module.product.service.ProductImportExportService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/products", "/api/products"})
public class ProductController {

    private final ProductService productService;
    private final ProductImportExportService productImportExportService;

    public ProductController(ProductService productService, ProductImportExportService productImportExportService) {
        this.productService = productService;
        this.productImportExportService = productImportExportService;
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

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('PRODUCT_IMPORT') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public BaseResponse<ImportJobResponse> importProducts(@RequestPart("file") MultipartFile file) {
        return BaseResponse.ok("Import job created successfully", productImportExportService.startImport(file));
    }

    @GetMapping("/export")
    @PreAuthorize("hasAuthority('PRODUCT_EXPORT') or hasAnyRole('SUPER_ADMIN','ADMIN','STORE_MANAGER','STORE_OWNER')")
    public ResponseEntity<Resource> exportProducts() {
        Resource csv = productImportExportService.exportCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"products.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
