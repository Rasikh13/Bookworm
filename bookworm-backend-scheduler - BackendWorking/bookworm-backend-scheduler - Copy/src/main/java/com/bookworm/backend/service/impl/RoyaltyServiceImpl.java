package com.bookworm.backend.service.impl;

import com.bookworm.backend.dto.response.RoyaltyLedgerResponse;
import com.bookworm.backend.dto.response.RoyaltySummaryResponse;
import com.bookworm.backend.entity.Beneficiary;
import com.bookworm.backend.entity.Product;
import com.bookworm.backend.entity.ProductBeneficiary;
import com.bookworm.backend.entity.RoyaltyLedger;
import com.bookworm.backend.exception.ResourceNotFoundException;
import com.bookworm.backend.mapper.RoyaltyMapper;
import com.bookworm.backend.repository.BeneficiaryRepository;
import com.bookworm.backend.repository.ProductBeneficiaryRepository;
import com.bookworm.backend.repository.RoyaltyLedgerRepository;
import com.bookworm.backend.service.RoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Reads ProductBeneficiary for the product being sold/rented and writes one
 * RoyaltyLedger row per configured beneficiary. If royalty percentages for
 * a product don't sum to 100, that's a data-entry problem in the
 * PRODUCT_BENEFICIARIES admin screen, not something this service corrects -
 * it splits exactly what's configured, whatever that sums to.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoyaltyServiceImpl implements RoyaltyService {

    private final ProductBeneficiaryRepository productBeneficiaryRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final RoyaltyLedgerRepository royaltyLedgerRepository;
    private final RoyaltyMapper mapper;

    @Override
    @Transactional
    public void recordForProduct(Product product, BigDecimal grossAmount,
                                  RoyaltyLedger.SourceType sourceType, Long sourceReferenceId) {
        List<ProductBeneficiary> splits = productBeneficiaryRepository.findByProduct_ProductId(product.getProductId());
        if (splits.isEmpty()) {
            // No beneficiaries configured for this product - nothing to accrue. Not an error;
            // plenty of catalog items (e.g. house-owned content) may have none.
            return;
        }

        for (ProductBeneficiary split : splits) {
            BigDecimal royaltyAmount = grossAmount
                    .multiply(split.getRoyaltyPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            royaltyLedgerRepository.save(RoyaltyLedger.builder()
                    .beneficiary(split.getBeneficiary())
                    .product(product)
                    .sourceType(sourceType)
                    .sourceReferenceId(sourceReferenceId)
                    .grossAmount(grossAmount)
                    .royaltyPercentage(split.getRoyaltyPercentage())
                    .royaltyAmount(royaltyAmount)
                    .build());
        }
    }

    @Override
    public Page<RoyaltyLedgerResponse> getHistory(Long beneficiaryId, Pageable pageable) {
        return royaltyLedgerRepository.findByBeneficiary_BeneficiaryId(beneficiaryId, pageable)
                .map(mapper::toResponse);
    }

    @Override
    public RoyaltySummaryResponse getSummary(Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary", "beneficiary_id", beneficiaryId));

        BigDecimal total = royaltyLedgerRepository.sumRoyaltyAmountByBeneficiary(beneficiaryId);
        return RoyaltySummaryResponse.builder()
                .beneficiaryId(beneficiary.getBeneficiaryId())
                .beneficiaryName(beneficiary.getName())
                .totalRoyaltyEarned(total)
                .build();
    }
}
