package com.bookworm.backend.repository;

import com.bookworm.backend.entity.RoyaltyLedger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface RoyaltyLedgerRepository extends JpaRepository<RoyaltyLedger, Long> {

    Page<RoyaltyLedger> findByBeneficiary_BeneficiaryId(Long beneficiaryId, Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(r.royaltyAmount), 0) FROM RoyaltyLedger r
            WHERE r.beneficiary.beneficiaryId = :beneficiaryId
            """)
    BigDecimal sumRoyaltyAmountByBeneficiary(@Param("beneficiaryId") Long beneficiaryId);
}
