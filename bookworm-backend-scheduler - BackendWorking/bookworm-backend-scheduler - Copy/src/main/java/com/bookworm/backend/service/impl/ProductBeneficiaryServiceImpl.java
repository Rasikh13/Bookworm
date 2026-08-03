package com.bookworm.backend.service.impl;

import com.bookworm.backend.dto.request.ProductBeneficiaryRequest;
import com.bookworm.backend.dto.response.ProductBeneficiaryResponse;
import com.bookworm.backend.entity.Beneficiary;
import com.bookworm.backend.entity.Product;
import com.bookworm.backend.entity.ProductBeneficiary;
import com.bookworm.backend.exception.DuplicateResourceException;
import com.bookworm.backend.exception.ResourceNotFoundException;
import com.bookworm.backend.mapper.ProductBeneficiaryMapper;
import com.bookworm.backend.repository.BeneficiaryRepository;
import com.bookworm.backend.repository.ProductBeneficiaryRepository;
import com.bookworm.backend.repository.ProductRepository;
import com.bookworm.backend.service.ProductBeneficiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductBeneficiaryServiceImpl implements ProductBeneficiaryService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final ProductBeneficiaryRepository productBeneficiaryRepository;
    private final ProductRepository productRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final ProductBeneficiaryMapper mapper;

    @Override
    public List<ProductBeneficiaryResponse> getByProduct(Long productId) {
        return productBeneficiaryRepository.findByProduct_ProductId(productId)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ProductBeneficiaryResponse addSplit(Long productId, ProductBeneficiaryRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "product_id", productId));
        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary", "beneficiary_id", request.getBeneficiaryId()));

        if (productBeneficiaryRepository.existsByProduct_ProductIdAndBeneficiary_BeneficiaryId(
                productId, request.getBeneficiaryId())) {
            throw new DuplicateResourceException("This beneficiary already has a royalty split on this product");
        }

        // Business rule (not enforced by the DB schema): a product's total royalty allocation
        // must not exceed 100%, since ROYALTY_LEDGER will later pay out amount = revenue * percentage
        // per beneficiary, and an over-100% total would double-pay revenue that doesn't exist.
        BigDecimal existingTotal = productBeneficiaryRepository.sumRoyaltyPercentageByProduct(productId);
        BigDecimal newTotal = existingTotal.add(request.getRoyaltyPercentage());
        if (newTotal.compareTo(HUNDRED) > 0) {
            throw new IllegalArgumentException(
                    "Total royalty percentage for this product would be " + newTotal
                            + "%, which exceeds 100% (currently allocated: " + existingTotal + "%)");
        }

        ProductBeneficiary split = ProductBeneficiary.builder()
                .product(product)
                .beneficiary(beneficiary)
                .royaltyPercentage(request.getRoyaltyPercentage())
                .build();

        return mapper.toResponse(productBeneficiaryRepository.save(split));
    }

    @Override
    @Transactional
    public void removeSplit(Long productId, Long productBeneficiaryId) {
        ProductBeneficiary split = productBeneficiaryRepository.findById(productBeneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductBeneficiary", "product_beneficiary_id", productBeneficiaryId));
        if (!split.getProduct().getProductId().equals(productId)) {
            throw new ResourceNotFoundException("ProductBeneficiary", "product_beneficiary_id", productBeneficiaryId);
        }
        productBeneficiaryRepository.delete(split);
    }
}
