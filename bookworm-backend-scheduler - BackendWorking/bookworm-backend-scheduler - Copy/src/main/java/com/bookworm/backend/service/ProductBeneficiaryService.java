package com.bookworm.backend.service;

import com.bookworm.backend.dto.request.ProductBeneficiaryRequest;
import com.bookworm.backend.dto.response.ProductBeneficiaryResponse;

import java.util.List;

public interface ProductBeneficiaryService {
    List<ProductBeneficiaryResponse> getByProduct(Long productId);
    ProductBeneficiaryResponse addSplit(Long productId, ProductBeneficiaryRequest request);
    void removeSplit(Long productId, Long productBeneficiaryId);
}
