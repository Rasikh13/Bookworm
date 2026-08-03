package com.bookworm.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Append-only royalty accrual, one row per (beneficiary, revenue event).
 * royaltyPercentage is snapshotted from ProductBeneficiary at the moment of
 * the sale/rental - if the split changes later, past accruals must not
 * silently change. sourceType + sourceReferenceId trace back to the
 * PurchaseItem/RentTransaction that generated it, same pattern as
 * LoyaltyLedger's referenceType/referenceId.
 */
@Entity
@Table(name = "ROYALTY_LEDGER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoyaltyLedger {

    public enum SourceType {
        PURCHASE, RENT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "royalty_ledger_id")
    private Long royaltyLedgerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_id", nullable = false)
    private Beneficiary beneficiary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private SourceType sourceType;

    @Column(name = "source_reference_id", nullable = false)
    private Long sourceReferenceId;

    @Column(name = "gross_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal grossAmount;

    @Column(name = "royalty_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal royaltyPercentage;

    @Column(name = "royalty_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal royaltyAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
