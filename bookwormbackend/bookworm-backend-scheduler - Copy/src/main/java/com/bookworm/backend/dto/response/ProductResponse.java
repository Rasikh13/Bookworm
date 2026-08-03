package com.bookworm.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponse {
    private Long productId;

    private Long subcategoryId;
    private String subcategoryName;
    private Long genreId;
    private String genreName;
    private Long languageId;
    private String languageName;

    private String title;
    private String shortDescription;
    private String description;
    private BigDecimal price;
    private Integer pages;
    private Integer duration;
    private String coverImage;
    private String fileType;

    private Boolean isRentable;
    private Boolean isLibraryEligible;
    private BigDecimal rentRate;
    private Integer minRentDays;
    private Boolean isAvailable;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
