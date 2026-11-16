package com.stratops.stratops360.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rule_threshold_configs")
public class RuleThresholdConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal minIncome = new BigDecimal("50000.00");

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal maxCarRatio = new BigDecimal("10.00");

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal maxMortgageRatio = new BigDecimal("20.00");

    @Column(nullable = false)
    private Integer minTenureYears = 4;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal maxOtherDebtRatio = new BigDecimal("5.00");

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tier1Limit = new BigDecimal("10000.00");

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal tier2Limit = new BigDecimal("3000.00");

    private LocalDateTime updatedAt = LocalDateTime.now();
    private String updatedBy = "System Administrator";

    public RuleThresholdConfig() {
    }

    public RuleThresholdConfig(BigDecimal minIncome, BigDecimal maxCarRatio, BigDecimal maxMortgageRatio,
                               Integer minTenureYears, BigDecimal maxOtherDebtRatio, BigDecimal tier1Limit,
                               BigDecimal tier2Limit, String updatedBy) {
        this.minIncome = minIncome;
        this.maxCarRatio = maxCarRatio;
        this.maxMortgageRatio = maxMortgageRatio;
        this.minTenureYears = minTenureYears;
        this.maxOtherDebtRatio = maxOtherDebtRatio;
        this.tier1Limit = tier1Limit;
        this.tier2Limit = tier2Limit;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMinIncome() {
        return minIncome;
    }

    public void setMinIncome(BigDecimal minIncome) {
        this.minIncome = minIncome;
    }

    public BigDecimal getMaxCarRatio() {
        return maxCarRatio;
    }

    public void setMaxCarRatio(BigDecimal maxCarRatio) {
        this.maxCarRatio = maxCarRatio;
    }

    public BigDecimal getMaxMortgageRatio() {
        return maxMortgageRatio;
    }

    public void setMaxMortgageRatio(BigDecimal maxMortgageRatio) {
        this.maxMortgageRatio = maxMortgageRatio;
    }

    public Integer getMinTenureYears() {
        return minTenureYears;
    }

    public void setMinTenureYears(Integer minTenureYears) {
        this.minTenureYears = minTenureYears;
    }

    public BigDecimal getMaxOtherDebtRatio() {
        return maxOtherDebtRatio;
    }

    public void setMaxOtherDebtRatio(BigDecimal maxOtherDebtRatio) {
        this.maxOtherDebtRatio = maxOtherDebtRatio;
    }

    public BigDecimal getTier1Limit() {
        return tier1Limit;
    }

    public void setTier1Limit(BigDecimal tier1Limit) {
        this.tier1Limit = tier1Limit;
    }

    public BigDecimal getTier2Limit() {
        return tier2Limit;
    }

    public void setTier2Limit(BigDecimal tier2Limit) {
        this.tier2Limit = tier2Limit;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuleThresholdConfig that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
