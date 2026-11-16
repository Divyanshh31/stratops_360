package com.stratops.stratops360.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Encapsulates rule thresholds derived from Slide 9 ("Rules in an Expert System").
 */
@Configuration
@ConfigurationProperties(prefix = "stratops.expert")
public class ExpertSystemProperties {

    private BigDecimal minIncome = new BigDecimal("50000.0");
    private BigDecimal maxCarRatio = new BigDecimal("10.0");
    private BigDecimal maxMortgageRatio = new BigDecimal("20.0");
    private int minTenureYears = 4;
    private BigDecimal maxOtherDebtRatio = new BigDecimal("5.0");
    private BigDecimal tier1Limit = new BigDecimal("10000.0");
    private BigDecimal tier2Limit = new BigDecimal("3000.0");

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

    public int getMinTenureYears() {
        return minTenureYears;
    }

    public void setMinTenureYears(int minTenureYears) {
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
