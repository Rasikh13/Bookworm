package com.bookworm.backend.repository;

import com.bookworm.backend.entity.LoyaltyLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoyaltyLedgerRepository extends JpaRepository<LoyaltyLedger, Long> {
    List<LoyaltyLedger> findByUserId(Long userId);
    Optional<LoyaltyLedger> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}
