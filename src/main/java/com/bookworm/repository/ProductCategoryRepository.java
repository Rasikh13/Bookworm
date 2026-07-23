package com.bookworm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookworm.entity.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    // Find category by name (case-insensitive)
    Optional<ProductCategory> findByCategoryNameIgnoreCase(String categoryName);

    // Check if category already exists
    boolean existsByCategoryNameIgnoreCase(String categoryName);
}