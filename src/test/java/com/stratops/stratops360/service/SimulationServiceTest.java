package com.stratops.stratops360.service;

import com.stratops.stratops360.config.SimulationProperties;
import com.stratops.stratops360.dto.SimulationFormDto;
import com.stratops.stratops360.model.SavedScenario;
import com.stratops.stratops360.model.ScenarioSimulationResult;
import com.stratops.stratops360.repository.SavedScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock
    private SavedScenarioRepository scenarioRepository;

    private SimulationProperties properties;
    private SimulationService simulationService;

    @BeforeEach
    void setUp() {
        properties = new SimulationProperties();
        properties.setBaseOperatingMargin(new BigDecimal("15.4"));
        properties.setBaseNetProfit(new BigDecimal("44.5"));
        properties.setBaseCustomerRetention(new BigDecimal("81.5"));
        properties.setBaseNps(new BigDecimal("68.0"));
        properties.setBaseCycleTime(new BigDecimal("6.8"));
        properties.setBaseDefectRate(new BigDecimal("1.3"));
        properties.setBaseEmployeeRetention(new BigDecimal("92.8"));

        simulationService = new SimulationService(properties, scenarioRepository);
    }

    @Test
    @DisplayName("Status Quo: Simulation with zero deltas preserves base scorecard metrics")
    void testZeroDeltaSimulation() {
        SimulationFormDto form = new SimulationFormDto();
        form.setScenarioName("Status Quo Baseline");
        form.setPriceAdjustmentPct(0.0);
        form.setMarketingSpendDeltaPct(0.0);
        form.setRawMaterialCostDeltaPct(0.0);
        form.setTrainingInvestmentDeltaPct(0.0);

        ScenarioSimulationResult result = simulationService.simulate(form);

        assertNotNull(result);
        assertEquals(15.4, result.getSimulatedOperatingMargin(), 0.01);
        assertEquals(44.5, result.getSimulatedNetProfit(), 0.01);
        assertEquals(81.5, result.getSimulatedCustomerRetention(), 0.01);
        assertEquals(6.8, result.getSimulatedCycleTime(), 0.01);
        assertEquals("LOW RISK / OPTIMAL", result.getRiskLevel());
    }

    @Test
    @DisplayName("Price Increase (+10%) with Marketing Expansion (+20%)")
    void testPriceAndMarketingExpansion() {
        SimulationFormDto form = new SimulationFormDto();
        form.setScenarioName("Premium Growth");
        form.setPriceAdjustmentPct(10.0);
        form.setMarketingSpendDeltaPct(20.0);
        form.setRawMaterialCostDeltaPct(0.0);
        form.setTrainingInvestmentDeltaPct(15.0);

        ScenarioSimulationResult result = simulationService.simulate(form);

        assertNotNull(result);
        assertTrue(result.getSimulatedOperatingMargin() > 15.4);
        assertTrue(result.getSimulatedNetProfit() > 44.5);
        assertFalse(result.getStrategicInsights().isEmpty());
    }

    @Test
    @DisplayName("Adverse Inflation Shock: Raw Material +35% with Price Cut -15% triggers HIGH RISK")
    void testAdverseShockTriggersHighRisk() {
        SimulationFormDto form = new SimulationFormDto();
        form.setScenarioName("Severe Cost Squeeze");
        form.setPriceAdjustmentPct(-15.0);
        form.setMarketingSpendDeltaPct(-20.0);
        form.setRawMaterialCostDeltaPct(35.0);
        form.setTrainingInvestmentDeltaPct(-30.0);

        ScenarioSimulationResult result = simulationService.simulate(form);

        assertNotNull(result);
        assertTrue(result.getSimulatedOperatingMargin() < 12.0);
        assertEquals("HIGH RISK", result.getRiskLevel());
    }

    @Test
    @DisplayName("Persist Scenario: Flagging saveScenario calls SavedScenarioRepository")
    void testPersistScenario() {
        when(scenarioRepository.save(any(SavedScenario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SimulationFormDto form = new SimulationFormDto();
        form.setScenarioName("Saved Q3 Plan");
        form.setDescription("Strategic hypothesis for Board meeting");
        form.setPriceAdjustmentPct(5.0);
        form.setMarketingSpendDeltaPct(10.0);
        form.setRawMaterialCostDeltaPct(2.0);
        form.setTrainingInvestmentDeltaPct(20.0);
        form.setSaveScenario(true);

        ScenarioSimulationResult result = simulationService.simulate(form);

        assertNotNull(result);
        verify(scenarioRepository, times(1)).save(any(SavedScenario.class));
    }
}
