package com.bookworm.service;

import java.util.List;

import com.bookworm.dto.ProductCategoryRequest;
import com.bookworm.dto.ProductCategoryResponse;

public interface ProductCategoryService {

    ProductCategoryResponse createCategory(ProductCategoryRequest request);

    List<ProductCategoryResponse> getAllCategories();

    ProductCategoryResponse getCategoryById(Long categoryId);

    ProductCategoryResponse updateCategory(Long categoryId, ProductCategoryRequest request);

    void deleteCategory(Long categoryId);
}