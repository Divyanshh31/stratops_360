package com.stratops.stratops360.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RuleThresholdConfigFormDto {

    @NotNull(message = "Minimum income threshold is required")
    @DecimalMin(value = "1000.00", message = "Minimum income must be at least $1,000")
    private BigDecimal minIncome;

    @NotNull(message = "Maximum car payment debt ratio is required")
    @DecimalMin(value = "1.00", message = "Car payment ceiling must be at least 1%")
    private BigDecimal maxCarRatio;

    @NotNull(message = "Maximum mortgage debt ratio is required")
    @DecimalMin(value = "1.00", message = "Mortgage payment ceiling must be at least 1%")
    private BigDecimal maxMortgageRatio;

    @NotNull(message = "Minimum tenure is required")
    @Min(value = 1, message = "Tenure must be at least 1 year")
    private Integer minTenureYears;

    @NotNull(message = "Maximum other debt ratio is required")
    @DecimalMin(value = "1.00", message = "Other debt ceiling must be at least 1%")
    private BigDecimal maxOtherDebtRatio;

    @NotNull(message = "Tier 1 credit limit is required")
    @DecimalMin(value = "500.00", message = "Tier 1 limit must be at least $500")
    private BigDecimal tier1Limit;

    @NotNull(message = "Tier 2 credit limit is required")
    @DecimalMin(value = "100.00", message = "Tier 2 limit must be at least $100")
    private BigDecimal tier2Limit;

    public RuleThresholdConfigFormDto() {
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
}
