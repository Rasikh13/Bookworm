package com.bookworm.backend.service.impl;

import com.bookworm.backend.dto.request.ProductRequest;
import com.bookworm.backend.dto.response.ProductResponse;
import com.bookworm.backend.entity.Genre;
import com.bookworm.backend.entity.Language;
import com.bookworm.backend.entity.Product;
import com.bookworm.backend.entity.ProductSubcategory;
import com.bookworm.backend.exception.ResourceNotFoundException;
import com.bookworm.backend.mapper.ProductMapper;
import com.bookworm.backend.repository.GenreRepository;
import com.bookworm.backend.repository.LanguageRepository;
import com.bookworm.backend.repository.ProductRepository;
import com.bookworm.backend.repository.ProductSubcategoryRepository;
import com.bookworm.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductSubcategoryRepository subcategoryRepository;
    private final GenreRepository genreRepository;
    private final LanguageRepository languageRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponse> browse(Long subcategoryId, Long genreId, Long languageId,
                                         String keyword, Pageable pageable) {
        Page<Product> page;
        if (StringUtils.hasText(keyword)) {
            page = productRepository.searchByTitle(keyword.trim(), pageable);
        } else if (subcategoryId != null) {
            page = productRepository.findByIsAvailableTrueAndSubcategory_SubcategoryId(subcategoryId, pageable);
        } else if (genreId != null) {
            page = productRepository.findByIsAvailableTrueAndGenre_GenreId(genreId, pageable);
        } else if (languageId != null) {
            page = productRepository.findByIsAvailableTrueAndLanguage_LanguageId(languageId, pageable);
        } else {
            page = productRepository.findByIsAvailableTrue(pageable);
        }
        return page.map(productMapper::toResponse);
    }

    @Override
    public ProductResponse getById(Long productId) {
        return productMapper.toResponse(findEntityOrThrow(productId));
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        validateRentFields(request);

        ProductSubcategory subcategory = subcategoryRepository.findById(request.getSubcategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Subcategory", "subcategory_id", request.getSubcategoryId()));
        Genre genre = resolveGenre(request.getGenreId());
        Language language = languageRepository.findById(request.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Language", "language_id", request.getLanguageId()));

        Product product = Product.builder()
                .subcategory(subcategory)
                .genre(genre)
                .language(language)
                .title(request.getTitle())
                .shortDescription(request.getShortDescription())
                .description(request.getDescription())
                .price(request.getPrice())
                .pages(request.getPages())
                .duration(request.getDuration())
                .coverImage(request.getCoverImage())
                .filePath(request.getFilePath())
                .fileType(request.getFileType())
                .isRentable(request.getIsRentable())
                .isLibraryEligible(request.getIsLibraryEligible())
                .rentRate(request.getIsRentable() ? request.getRentRate() : null)
                .minRentDays(request.getIsRentable() ? request.getMinRentDays() : null)
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .build();

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long productId, ProductRequest request) {
        validateRentFields(request);

        Product product = findEntityOrThrow(productId);

        if (!product.getSubcategory().getSubcategoryId().equals(request.getSubcategoryId())) {
            ProductSubcategory subcategory = subcategoryRepository.findById(request.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subcategory", "subcategory_id", request.getSubcategoryId()));
            product.setSubcategory(subcategory);
        }
        product.setGenre(resolveGenre(request.getGenreId()));
        if (!product.getLanguage().getLanguageId().equals(request.getLanguageId())) {
            Language language = languageRepository.findById(request.getLanguageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Language", "language_id", request.getLanguageId()));
            product.setLanguage(language);
        }

        product.setTitle(request.getTitle());
        product.setShortDescription(request.getShortDescription());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setPages(request.getPages());
        product.setDuration(request.getDuration());
        product.setCoverImage(request.getCoverImage());
        product.setFilePath(request.getFilePath());
        product.setFileType(request.getFileType());
        product.setIsRentable(request.getIsRentable());
        product.setIsLibraryEligible(request.getIsLibraryEligible());
        product.setRentRate(request.getIsRentable() ? request.getRentRate() : null);
        product.setMinRentDays(request.getIsRentable() ? request.getMinRentDays() : null);
        if (request.getIsAvailable() != null) product.setIsAvailable(request.getIsAvailable());

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Product product = findEntityOrThrow(productId);
        // Soft delete only. Products are referenced by CART_ITEMS, PURCHASE_ITEMS, RENT_TRANSACTIONS,
        // USER_LIBRARY etc. - a hard delete would either violate FK constraints or destroy purchase
        // history for users who already own it. is_available=false just pulls it off the storefront.
        product.setIsAvailable(false);
        productRepository.save(product);
    }

    /** is_rentable=true requires rent_rate and min_rent_days; is_rentable=false must not carry stale rent data. */
    private void validateRentFields(ProductRequest request) {
        if (Boolean.TRUE.equals(request.getIsRentable())) {
            if (request.getRentRate() == null || request.getMinRentDays() == null) {
                throw new IllegalArgumentException(
                        "rentRate and minRentDays are required when isRentable is true");
            }
        }
    }

    private Genre resolveGenre(Long genreId) {
        if (genreId == null) return null;
        return genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre", "genre_id", genreId));
    }

    private Product findEntityOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "product_id", productId));
    }
}
