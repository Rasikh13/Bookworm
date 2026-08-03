package com.bookworm.backend.mapper;

import com.bookworm.backend.dto.response.ProductResponse;
import com.bookworm.backend.entity.Genre;
import com.bookworm.backend.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product entity) {
        Genre genre = entity.getGenre();
        return ProductResponse.builder()
                .productId(entity.getProductId())
                .subcategoryId(entity.getSubcategory().getSubcategoryId())
                .subcategoryName(entity.getSubcategory().getSubcategoryName())
                .genreId(genre != null ? genre.getGenreId() : null)
                .genreName(genre != null ? genre.getGenreName() : null)
                .languageId(entity.getLanguage().getLanguageId())
                .languageName(entity.getLanguage().getLanguageName())
                .title(entity.getTitle())
                .shortDescription(entity.getShortDescription())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .pages(entity.getPages())
                .duration(entity.getDuration())
                .coverImage(entity.getCoverImage())
                .fileType(entity.getFileType())
                // file_path intentionally excluded - it's the raw content location and must only
                // ever be resolved server-side after ownership/rental/library access is verified,
                // never handed to the client on a catalog browse response.
                .isRentable(entity.getIsRentable())
                .isLibraryEligible(entity.getIsLibraryEligible())
                .rentRate(entity.getRentRate())
                .minRentDays(entity.getMinRentDays())
                .isAvailable(entity.getIsAvailable())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
