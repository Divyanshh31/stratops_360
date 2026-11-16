package com.stratops.stratops360.dto;

import jakarta.validation.constraints.*;

public class SimulationFormDto {

    @NotBlank(message = "Scenario name is required")
    @Size(max = 120, message = "Scenario name must be 120 characters or fewer")
    private String scenarioName = "Baseline Sensitivity Model";

    private String description;

    @NotNull(message = "Price adjustment percentage is required")
    @Min(value = -30, message = "Price adjustment cannot be less than -30%")
    @Max(value = 30, message = "Price adjustment cannot exceed +30%")
    private Double priceAdjustmentPct = 0.0;

    @NotNull(message = "Marketing spend delta percentage is required")
    @Min(value = -50, message = "Marketing budget reduction cannot exceed -50%")
    @Max(value = 50, message = "Marketing budget expansion cannot exceed +50%")
    private Double marketingSpendDeltaPct = 0.0;

    @NotNull(message = "Raw material cost delta percentage is required")
    @Min(value = -20, message = "Raw material cost rebate cannot exceed -20%")
    @Max(value = 40, message = "Raw material inflation cannot exceed +40%")
    private Double rawMaterialCostDeltaPct = 0.0;

    @NotNull(message = "Training investment delta percentage is required")
    @Min(value = -50, message = "Training budget cut cannot exceed -50%")
    @Max(value = 100, message = "Training budget increase cannot exceed +100%")
    private Double trainingInvestmentDeltaPct = 0.0;

    private boolean saveScenario = false;

    public SimulationFormDto() {
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScenarioDescription() {
        return description;
    }

    public void setScenarioDescription(String scenarioDescription) {
        this.description = scenarioDescription;
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

    public boolean isSaveScenario() {
        return saveScenario;
    }

    public void setSaveScenario(boolean saveScenario) {
        this.saveScenario = saveScenario;
    }
}
