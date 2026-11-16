package com.stratops.stratops360.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "saved_scenarios")
public class SavedScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private Double priceAdjustmentPct;
    private Double marketingSpendDeltaPct;
    private Double rawMaterialCostDeltaPct;
    private Double trainingInvestmentDeltaPct;

    private Double simulatedOperatingMargin;
    private Double simulatedNetProfit;
    private Double simulatedCustomerRetention;
    private Double simulatedCycleTime;

    private String riskLevel;

    private LocalDateTime createdAt = LocalDateTime.now();

    public SavedScenario() {
    }

    public SavedScenario(String name, String description, Double priceAdjustmentPct, Double marketingSpendDeltaPct,
                         Double rawMaterialCostDeltaPct, Double trainingInvestmentDeltaPct, Double simulatedOperatingMargin,
                         Double simulatedNetProfit, Double simulatedCustomerRetention, Double simulatedCycleTime, String riskLevel) {
        this.name = name;
        this.description = description;
        this.priceAdjustmentPct = priceAdjustmentPct;
        this.marketingSpendDeltaPct = marketingSpendDeltaPct;
        this.rawMaterialCostDeltaPct = rawMaterialCostDeltaPct;
        this.trainingInvestmentDeltaPct = trainingInvestmentDeltaPct;
        this.simulatedOperatingMargin = simulatedOperatingMargin;
        this.simulatedNetProfit = simulatedNetProfit;
        this.simulatedCustomerRetention = simulatedCustomerRetention;
        this.simulatedCycleTime = simulatedCycleTime;
        this.riskLevel = riskLevel;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPriceAdjustmentPct() {
        return priceAdjustmentPct;
    }

    public void setPriceAdjustmentPct(Double priceAdjustmentPct) {
        this.priceAdjustmentPct = priceAdjustmentPct;
    }

    public Double getMarketingSpendDeltaPct() {
        return marketingSpendDeltaPct;
    }

    public void setMarketingSpendDeltaPct(Double marketingSpendDeltaPct) {
        this.marketingSpendDeltaPct = marketingSpendDeltaPct;
    }

    public Double getRawMaterialCostDeltaPct() {
        return rawMaterialCostDeltaPct;
    }

    public void setRawMaterialCostDeltaPct(Double rawMaterialCostDeltaPct) {
        this.rawMaterialCostDeltaPct = rawMaterialCostDeltaPct;
    }

    public Double getTrainingInvestmentDeltaPct() {
        return trainingInvestmentDeltaPct;
    }

    public void setTrainingInvestmentDeltaPct(Double trainingInvestmentDeltaPct) {
        this.trainingInvestmentDeltaPct = trainingInvestmentDeltaPct;
    }

    public Double getSimulatedOperatingMargin() {
        return simulatedOperatingMargin;
    }

    public void setSimulatedOperatingMargin(Double simulatedOperatingMargin) {
        this.simulatedOperatingMargin = simulatedOperatingMargin;
    }

    public Double getSimulatedNetProfit() {
        return simulatedNetProfit;
    }

    public void setSimulatedNetProfit(Double simulatedNetProfit) {
        this.simulatedNetProfit = simulatedNetProfit;
    }

    public Double getSimulatedCustomerRetention() {
        return simulatedCustomerRetention;
    }

    public void setSimulatedCustomerRetention(Double simulatedCustomerRetention) {
        this.simulatedCustomerRetention = simulatedCustomerRetention;
    }

    public Double getSimulatedCycleTime() {
        return simulatedCycleTime;
    }

    public void setSimulatedCycleTime(Double simulatedCycleTime) {
        this.simulatedCycleTime = simulatedCycleTime;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SavedScenario that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
