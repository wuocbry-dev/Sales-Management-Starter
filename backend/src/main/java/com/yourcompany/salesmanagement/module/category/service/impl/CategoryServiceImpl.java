package com.yourcompany.salesmanagement.module.category.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.category.dto.request.CreateCategoryRequest;
import com.yourcompany.salesmanagement.module.category.dto.response.CategoryResponse;
import com.yourcompany.salesmanagement.module.category.entity.Category;
import com.yourcompany.salesmanagement.module.category.repository.CategoryRepository;
import com.yourcompany.salesmanagement.module.category.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getCategories() {
        Long storeId = SecurityUtils.requireStoreId();
        return categoryRepository.findAllByStoreId(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Category c = new Category();
        c.setStoreId(storeId);
        c.setName(request.name());
        c = categoryRepository.save(c);
        return toResponse(c);
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName());
    }
}

