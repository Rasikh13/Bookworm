package com.bookworm.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bookworm.dto.ProductCategoryRequest;
import com.bookworm.dto.ProductCategoryResponse;
import com.bookworm.entity.ProductCategory;
import com.bookworm.exception.CategoryAlreadyExistsException;
import com.bookworm.exception.CategoryNotFoundException;
import com.bookworm.repository.ProductCategoryRepository;
import com.bookworm.service.ProductCategoryService;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    public ProductCategoryServiceImpl(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductCategoryResponse createCategory(ProductCategoryRequest request) {

        if (categoryRepository.existsByCategoryNameIgnoreCase(request.getCategoryName())) {
            throw new CategoryAlreadyExistsException("Category already exists.");
        }

        ProductCategory category = new ProductCategory();

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        category.setActive(request.getActive());

        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        ProductCategory savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Override
    public List<ProductCategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryResponse getCategoryById(Long categoryId) {

        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        return mapToResponse(category);
    }

    @Override
    public ProductCategoryResponse updateCategory(Long categoryId, ProductCategoryRequest request) {

        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        category.setActive(request.getActive());

        category.setUpdatedAt(LocalDateTime.now());

        ProductCategory updatedCategory = categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(Long categoryId) {

        ProductCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        categoryRepository.delete(category);
    }

    private ProductCategoryResponse mapToResponse(ProductCategory category) {

        ProductCategoryResponse response = new ProductCategoryResponse();

        response.setCategoryId(category.getCategoryId());
        response.setCategoryName(category.getCategoryName());
        response.setDescription(category.getDescription());
        response.setSortOrder(category.getSortOrder());
        response.setActive(category.getActive());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        return response;
    }
}