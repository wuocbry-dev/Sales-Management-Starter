package com.yourcompany.salesmanagement.module.category.service.impl;

import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.category.dto.request.CreateCategoryRequest;
import com.yourcompany.salesmanagement.module.category.dto.request.UpdateCategoryRequest;
import com.yourcompany.salesmanagement.module.category.dto.response.CategoryResponse;
import com.yourcompany.salesmanagement.module.category.entity.Category;
import com.yourcompany.salesmanagement.module.category.repository.CategoryRepository;
import com.yourcompany.salesmanagement.module.category.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        c.setStatus("ACTIVE");
        c = categoryRepository.save(c);
        return toResponse(c);
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {
        Long storeId = SecurityUtils.requireStoreId();
        Category c = categoryRepository.findByIdAndStoreId(id, storeId)
                .orElseThrow(() -> new BusinessException("Category not found", HttpStatus.NOT_FOUND));
        c.setName(request.name());
        if (request.status() != null && !request.status().isBlank()) {
            c.setStatus(request.status().trim());
        }
        c = categoryRepository.save(c);
        return toResponse(c);
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getStatus());
    }
}

