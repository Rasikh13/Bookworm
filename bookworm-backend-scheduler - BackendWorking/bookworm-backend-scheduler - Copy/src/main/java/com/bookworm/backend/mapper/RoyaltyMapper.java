package com.bookworm.backend.mapper;

import com.bookworm.backend.dto.response.RoyaltyLedgerResponse;
import com.bookworm.backend.entity.RoyaltyLedger;
import org.springframework.stereotype.Component;

@Component
public class RoyaltyMapper {

    public RoyaltyLedgerResponse toResponse(RoyaltyLedger entity) {
        return RoyaltyLedgerResponse.builder()
                .royaltyLedgerId(entity.getRoyaltyLedgerId())
                .beneficiaryId(entity.getBeneficiary().getBeneficiaryId())
                .beneficiaryName(entity.getBeneficiary().getName())
                .productId(entity.getProduct().getProductId())
                .productTitle(entity.getProduct().getTitle())
                .sourceType(entity.getSourceType())
                .sourceReferenceId(entity.getSourceReferenceId())
                .grossAmount(entity.getGrossAmount())
                .royaltyPercentage(entity.getRoyaltyPercentage())
                .royaltyAmount(entity.getRoyaltyAmount())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
