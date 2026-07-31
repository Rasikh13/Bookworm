package com.bookworm.backend.repository;

import com.bookworm.backend.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    List<Genre> findBySubcategory_SubcategoryIdAndIsActiveTrue(Long subcategoryId);

    List<Genre> findByIsActiveTrue();

    boolean existsByGenreNameIgnoreCaseAndSubcategory_SubcategoryId(String genreName, Long subcategoryId);
}
