package com.yourcompany.salesmanagement.module.category.service;

import com.yourcompany.salesmanagement.module.category.dto.request.CreateCategoryRequest;
import com.yourcompany.salesmanagement.module.category.dto.request.UpdateCategoryRequest;
import com.yourcompany.salesmanagement.module.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategories();

    CategoryResponse createCategory(CreateCategoryRequest request);

    CategoryResponse update(Long id, UpdateCategoryRequest request);
}

