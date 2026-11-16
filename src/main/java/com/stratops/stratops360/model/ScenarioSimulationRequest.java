package com.stratops.stratops360.model;

public class ScenarioSimulationRequest {

    // Percentage deltas (-50 to +50)
    private Double priceAdjustmentPct = 0.0;
    private Double marketingSpendDeltaPct = 0.0;
    private Double rawMaterialCostDeltaPct = 0.0;
    private Double trainingInvestmentDeltaPct = 0.0;
    private String scenarioName = "Baseline Scenario";

    public ScenarioSimulationRequest() {
    }

    public ScenarioSimulationRequest(Double priceAdjustmentPct, Double marketingSpendDeltaPct,
                                     Double rawMaterialCostDeltaPct, Double trainingInvestmentDeltaPct, String scenarioName) {
        this.priceAdjustmentPct = priceAdjustmentPct;
        this.marketingSpendDeltaPct = marketingSpendDeltaPct;
        this.rawMaterialCostDeltaPct = rawMaterialCostDeltaPct;
        this.trainingInvestmentDeltaPct = trainingInvestmentDeltaPct;
        this.scenarioName = scenarioName;
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

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }
}
