package com.yourcompany.salesmanagement.module.category.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.category.dto.request.CreateCategoryRequest;
import com.yourcompany.salesmanagement.module.category.dto.response.CategoryResponse;
import com.yourcompany.salesmanagement.module.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public BaseResponse<List<CategoryResponse>> getCategories() {
        return BaseResponse.ok("Categories fetched successfully", categoryService.getCategories());
    }

    @PostMapping
    public BaseResponse<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return BaseResponse.ok("Category created successfully", categoryService.createCategory(request));
    }
}

