package com.bookworm.backend.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductBeneficiaryRequest {

    @NotNull(message = "beneficiaryId is required")
    private Long beneficiaryId;

    @NotNull(message = "royaltyPercentage is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Royalty percentage must be greater than 0")
    @DecimalMax(value = "100.0", message = "Royalty percentage cannot exceed 100")
    private BigDecimal royaltyPercentage;
}
