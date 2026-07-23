package com.bookworm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.bookworm.dto.ProductCategoryRequest;
import com.bookworm.dto.ProductCategoryResponse;
import com.bookworm.service.ProductCategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
@Validated
public class ProductCategoryController {

    private final ProductCategoryService categoryService;

    public ProductCategoryController(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ProductCategoryResponse> createCategory(
            @Valid @RequestBody ProductCategoryRequest request) {

        ProductCategoryResponse response = categoryService.createCategory(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> getCategoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody ProductCategoryRequest request) {

        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.ok("Category deleted successfully.");
    }
}