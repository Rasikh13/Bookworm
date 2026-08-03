package com.bookworm.backend.repository;

import com.bookworm.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByIsAvailableTrue(Pageable pageable);

    Page<Product> findByIsAvailableTrueAndSubcategory_SubcategoryId(Long subcategoryId, Pageable pageable);

    Page<Product> findByIsAvailableTrueAndGenre_GenreId(Long genreId, Pageable pageable);

    Page<Product> findByIsAvailableTrueAndLanguage_LanguageId(Long languageId, Pageable pageable);

    // Basic title search - kept simple for now; can move to full-text search later if needed.
    @Query("""
            SELECT p FROM Product p
            WHERE p.isAvailable = true
            AND LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Product> searchByTitle(@Param("keyword") String keyword, Pageable pageable);
}
