package com.bookworm.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotNull(message = "subcategoryId is required")
    private Long subcategoryId;

    private Long genreId; // optional

    @NotNull(message = "languageId is required")
    private Long languageId;

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;

    @Size(max = 500)
    private String shortDescription;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal price;

    private Integer pages;
    private Integer duration;

    @Size(max = 255)
    private String coverImage;

    @Size(max = 255)
    private String filePath;

    @Size(max = 50)
    private String fileType;

    @NotNull(message = "isRentable must be specified")
    private Boolean isRentable;

    @NotNull(message = "isLibraryEligible must be specified")
    private Boolean isLibraryEligible;

    // Required only when isRentable = true - checked in the service, not here,
    // since Bean Validation can't easily express "required if another field is true"
    // without a custom cross-field validator.
    @DecimalMin(value = "0.0", message = "Rent rate cannot be negative")
    private BigDecimal rentRate;

    @Min(value = 1, message = "Minimum rent days must be at least 1")
    private Integer minRentDays;

    private Boolean isAvailable;
}
