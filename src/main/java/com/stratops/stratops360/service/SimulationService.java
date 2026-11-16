package com.stratops.stratops360.service;

import com.stratops.stratops360.config.SimulationProperties;
import com.stratops.stratops360.dto.SimulationFormDto;
import com.stratops.stratops360.model.SavedScenario;
import com.stratops.stratops360.model.ScenarioSimulationRequest;
import com.stratops.stratops360.model.ScenarioSimulationResult;
import com.stratops.stratops360.repository.SavedScenarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Slide 12: Business Intelligence (BI) Forecasting & What-If Scenario Analysis Service.
 */
@Service
@Transactional
public class SimulationService {

    // Documented Sensitivity Coefficients:
    // 1. Price Elasticity on Margin: +0.45% margin per +1% price hike
    private static final double PRICE_ON_MARGIN_COEFFICIENT = 0.45;
    // 2. Price Elasticity on Retention: -0.28% customer retention drop per +1% price hike without marketing
    private static final double PRICE_ON_RETENTION_COEFFICIENT = 0.28;
    // 3. Marketing Expansion: +0.14% customer retention gain per +1% marketing spend
    private static final double MKTG_ON_RETENTION_COEFFICIENT = 0.14;
    // 4. Marketing Expense Drag on Margin: -0.08% margin impact per +1% marketing spend
    private static final double MKTG_ON_MARGIN_COEFFICIENT = 0.08;
    // 5. Raw Material Cost Inflation: -0.55% margin compression per +1% COGS inflation
    private static final double RAW_MAT_ON_MARGIN_COEFFICIENT = 0.55;
    // 6. Workforce Training Synergy: -0.025 days cycle time and -0.015% defect rate per +1% training
    private static final double TRAINING_ON_CYCLE_TIME_COEFFICIENT = 0.025;
    private static final double TRAINING_ON_DEFECT_COEFFICIENT = 0.015;
    private static final double TRAINING_ON_EMPLOYEE_RETENTION_COEFFICIENT = 0.15;

    private final SimulationProperties properties;
    private final SavedScenarioRepository scenarioRepository;

    public SimulationService(SimulationProperties properties, SavedScenarioRepository scenarioRepository) {
        this.properties = properties;
        this.scenarioRepository = scenarioRepository;
    }

    @Transactional(readOnly = true)
    public List<SavedScenario> getSavedScenarios() {
        return scenarioRepository.findAllByOrderByCreatedAtDesc();
    }

    public void deleteSavedScenario(Long id) {
        scenarioRepository.deleteById(id);
    }

    public ScenarioSimulationResult simulate(SimulationFormDto form) {
        ScenarioSimulationRequest req = new ScenarioSimulationRequest(
                form.getPriceAdjustmentPct(),
                form.getMarketingSpendDeltaPct(),
                form.getRawMaterialCostDeltaPct(),
                form.getTrainingInvestmentDeltaPct(),
                form.getScenarioName()
        );

        ScenarioSimulationResult result = new ScenarioSimulationResult();
        result.setRequest(req);
        result.setScenarioName(form.getScenarioName());

        double baseMargin = properties.getBaseOperatingMargin().doubleValue();
        double baseNetProfit = properties.getBaseNetProfit().doubleValue();
        double baseRetention = properties.getBaseCustomerRetention().doubleValue();
        double baseNps = properties.getBaseNps().doubleValue();
        double baseCycleTime = properties.getBaseCycleTime().doubleValue();
        double baseDefectRate = properties.getBaseDefectRate().doubleValue();
        double baseEmpRetention = properties.getBaseEmployeeRetention().doubleValue();

        result.setBaseOperatingMargin(baseMargin);
        result.setBaseNetProfit(baseNetProfit);
        result.setBaseCustomerRetention(baseRetention);
        result.setBaseNps(baseNps);
        result.setBaseCycleTime(baseCycleTime);
        result.setBaseDefectRate(baseDefectRate);
        result.setBaseEmployeeRetention(baseEmpRetention);

        double dPrice = form.getPriceAdjustmentPct() != null ? form.getPriceAdjustmentPct() : 0.0;
        double dMktg = form.getMarketingSpendDeltaPct() != null ? form.getMarketingSpendDeltaPct() : 0.0;
        double dRawMat = form.getRawMaterialCostDeltaPct() != null ? form.getRawMaterialCostDeltaPct() : 0.0;
        double dTrain = form.getTrainingInvestmentDeltaPct() != null ? form.getTrainingInvestmentDeltaPct() : 0.0;

        // 1. Operating Margin
        double simMargin = baseMargin + (dPrice * PRICE_ON_MARGIN_COEFFICIENT)
                - (dRawMat * RAW_MAT_ON_MARGIN_COEFFICIENT)
                - (dMktg * MKTG_ON_MARGIN_COEFFICIENT);
        simMargin = Math.max(1.0, Math.min(45.0, simMargin));
        result.setSimulatedOperatingMargin(Math.round(simMargin * 10.0) / 10.0);

        // 2. Net Profit ($M)
        double profitMultiplier = (simMargin / baseMargin);
        double volumeFactor = 1.0 - (dPrice * 0.008) + (dMktg * 0.005);
        double simProfit = baseNetProfit * profitMultiplier * volumeFactor;
        result.setSimulatedNetProfit(Math.round(simProfit * 10.0) / 10.0);

        // 3. Customer Retention (%)
        double simRetention = baseRetention - (dPrice * PRICE_ON_RETENTION_COEFFICIENT)
                + (dMktg * MKTG_ON_RETENTION_COEFFICIENT);
        simRetention = Math.max(50.0, Math.min(99.0, simRetention));
        result.setSimulatedCustomerRetention(Math.round(simRetention * 10.0) / 10.0);

        // 4. NPS
        double simNps = baseNps - (dPrice * 0.40) + (dMktg * 0.25);
        simNps = Math.max(10.0, Math.min(95.0, simNps));
        result.setSimulatedNps(Math.round(simNps * 10.0) / 10.0);

        // 5. Order Cycle Time
        double simCycleTime = baseCycleTime - (dTrain * TRAINING_ON_CYCLE_TIME_COEFFICIENT);
        simCycleTime = Math.max(2.5, Math.min(14.0, simCycleTime));
        result.setSimulatedCycleTime(Math.round(simCycleTime * 10.0) / 10.0);

        // 6. Defect Rate
        double simDefect = baseDefectRate - (dTrain * TRAINING_ON_DEFECT_COEFFICIENT);
        simDefect = Math.max(0.3, Math.min(5.0, simDefect));
        result.setSimulatedDefectRate(Math.round(simDefect * 10.0) / 10.0);

        // 7. Employee Retention
        double simEmpRet = baseEmpRetention + (dTrain * TRAINING_ON_EMPLOYEE_RETENTION_COEFFICIENT);
        simEmpRet = Math.max(65.0, Math.min(99.5, simEmpRet));
        result.setSimulatedEmployeeRetention(Math.round(simEmpRet * 10.0) / 10.0);

        // Insights & Risk
        generateInsights(result, dPrice, dMktg, dRawMat, dTrain, baseMargin, baseNetProfit, baseRetention);

        // Persist scenario if requested
        if (form.isSaveScenario()) {
            SavedScenario scenario = new SavedScenario(
                    form.getScenarioName(),
                    form.getDescription() != null ? form.getDescription() : "Saved from What-If Simulator",
                    dPrice, dMktg, dRawMat, dTrain,
                    result.getSimulatedOperatingMargin(),
                    result.getSimulatedNetProfit(),
                    result.getSimulatedCustomerRetention(),
                    result.getSimulatedCycleTime(),
                    result.getRiskLevel()
            );
            scenarioRepository.save(scenario);
        }

        return result;
    }

    private void generateInsights(ScenarioSimulationResult result, double dPrice, double dMktg, double dRawMat,
                                  double dTrain, double baseMargin, double baseNetProfit, double baseRetention) {
        if (dPrice > 5.0 && dMktg < 5.0) {
            result.addInsight(String.format("⚠️ Price Elasticity Trade-Off: Operating margin increases to %.1f%%, but customer retention declines by %.1f%% without marketing buffer. Laudon Model: Semistructured decision requires executive evaluation.",
                    result.getSimulatedOperatingMargin(), (baseRetention - result.getSimulatedCustomerRetention())));
        }

        if (dRawMat > 5.0) {
            result.addInsight(String.format("🚨 Supply Chain Inflation: A +%.1f%% raw material price shock compresses net profit by $%.1fM. Recommendation: Activate Value Web Supplier Renegotiation protocol.",
                    dRawMat, (baseNetProfit - result.getSimulatedNetProfit())));
        }

        if (dTrain > 15.0) {
            result.addInsight(String.format("💡 Learning & Growth Synergy: Expanding workforce training by +%.1f%% drives defect rate to %.2f%% and reduces cycle time to %.1f days.",
                    dTrain, result.getSimulatedDefectRate(), result.getSimulatedCycleTime()));
        }

        if (dPrice < 0.0 && dMktg > 10.0) {
            result.addInsight(String.format("📈 Market Penetration Strategy: Price discounting expands customer base, but compresses Operating Margin to %.1f%%. Monitor operational cash flows closely.",
                    result.getSimulatedOperatingMargin()));
        }

        if (result.getStrategicInsights().isEmpty()) {
            result.addInsight("✅ Optimal Operational Equilibrium: Simulated levers maintain balanced scorecard performance within normal tolerance corridors.");
        }

        if (result.getSimulatedOperatingMargin() < 12.0 || result.getSimulatedCustomerRetention() < 75.0) {
            result.setRiskLevel("HIGH RISK");
        } else if (result.getSimulatedOperatingMargin() < 14.5 || result.getSimulatedCustomerRetention() < 80.0) {
            result.setRiskLevel("MODERATE RISK");
        } else {
            result.setRiskLevel("LOW RISK / OPTIMAL");
        }
    }
}
