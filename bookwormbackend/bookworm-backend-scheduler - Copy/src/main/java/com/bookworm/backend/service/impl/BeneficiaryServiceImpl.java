package com.bookworm.backend.service.impl;

import com.bookworm.backend.dto.request.BeneficiaryRequest;
import com.bookworm.backend.dto.response.BeneficiaryResponse;
import com.bookworm.backend.entity.Beneficiary;
import com.bookworm.backend.exception.ResourceNotFoundException;
import com.bookworm.backend.mapper.BeneficiaryMapper;
import com.bookworm.backend.repository.BeneficiaryRepository;
import com.bookworm.backend.service.BeneficiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final BeneficiaryMapper beneficiaryMapper;

    @Override
    public List<BeneficiaryResponse> getAllActive() {
        return beneficiaryRepository.findByIsActiveTrue().stream().map(beneficiaryMapper::toResponse).toList();
    }

    @Override
    public List<BeneficiaryResponse> getAll() {
        return beneficiaryRepository.findAll().stream().map(beneficiaryMapper::toResponse).toList();
    }

    @Override
    public BeneficiaryResponse getById(Long beneficiaryId) {
        return beneficiaryMapper.toResponse(findEntityOrThrow(beneficiaryId));
    }

    @Override
    @Transactional
    public BeneficiaryResponse create(BeneficiaryRequest request) {
        Beneficiary beneficiary = Beneficiary.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        return beneficiaryMapper.toResponse(beneficiaryRepository.save(beneficiary));
    }

    @Override
    @Transactional
    public BeneficiaryResponse update(Long beneficiaryId, BeneficiaryRequest request) {
        Beneficiary beneficiary = findEntityOrThrow(beneficiaryId);
        beneficiary.setName(request.getName());
        beneficiary.setDescription(request.getDescription());
        if (request.getIsActive() != null) beneficiary.setIsActive(request.getIsActive());
        return beneficiaryMapper.toResponse(beneficiaryRepository.save(beneficiary));
    }

    @Override
    @Transactional
    public void delete(Long beneficiaryId) {
        Beneficiary beneficiary = findEntityOrThrow(beneficiaryId);
        beneficiary.setIsActive(false);
        beneficiaryRepository.save(beneficiary);
    }

    private Beneficiary findEntityOrThrow(Long beneficiaryId) {
        return beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary", "beneficiary_id", beneficiaryId));
    }
}
