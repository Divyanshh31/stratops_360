package com.stratops.stratops360.service;

import com.stratops.stratops360.config.ExpertSystemProperties;
import com.stratops.stratops360.dto.CreditEvaluationFormDto;
import com.stratops.stratops360.model.CreditRuleEvaluation;
import com.stratops.stratops360.model.RuleThresholdConfig;
import com.stratops.stratops360.repository.CreditRuleEvaluationRepository;
import com.stratops.stratops360.repository.RuleThresholdConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExpertSystemEngineTest {

    @Mock
    private CreditRuleEvaluationRepository evaluationRepository;

    @Mock
    private RuleThresholdConfigRepository thresholdConfigRepository;

    private ExpertSystemProperties properties;
    private ExpertSystemEngine engine;

    @BeforeEach
    void setUp() {
        properties = new ExpertSystemProperties();
        properties.setMinIncome(new BigDecimal("50000.00"));
        properties.setMaxCarRatio(new BigDecimal("10.0"));
        properties.setMaxMortgageRatio(new BigDecimal("20.0"));
        properties.setMinTenureYears(4);
        properties.setMaxOtherDebtRatio(new BigDecimal("5.0"));
        properties.setTier1Limit(new BigDecimal("10000.00"));
        properties.setTier2Limit(new BigDecimal("3000.00"));

        RuleThresholdConfig config = new RuleThresholdConfig(
                new BigDecimal("50000.00"),
                new BigDecimal("10.0"),
                new BigDecimal("20.0"),
                4,
                new BigDecimal("5.0"),
                new BigDecimal("10000.00"),
                new BigDecimal("3000.00"),
                "Admin"
        );
        when(thresholdConfigRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(config));
        when(evaluationRepository.save(any(CreditRuleEvaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        engine = new ExpertSystemEngine(evaluationRepository, thresholdConfigRepository, properties);
    }

    @Test
    @DisplayName("Boundary: Income of exactly $50,000 fails Rule A (Must be strictly > $50,000)")
    void testBoundaryIncomeExactlyFiftyThousandFails() {
        CreditEvaluationFormDto dto = new CreditEvaluationFormDto(
                "John Doe",
                new BigDecimal("50000.00"),
                new BigDecimal("300.00"),
                new BigDecimal("800.00"),
                5,
                new BigDecimal("100.00")
        );

        CreditRuleEvaluation result = engine.evaluateAndSave(dto);

        assertFalse(result.isApproved());
        assertTrue(result.getOutcomeState().contains("Income Below Minimum"));
        assertEquals(BigDecimal.ZERO, result.getGrantedCreditLimit());
        assertTrue(result.getRuleFiringLog().contains("RULE_A_TO_B"));
        assertTrue(result.getRuleFiringLog().contains("Rule Failed"));
    }

    @Test
    @DisplayName("Income of $49,999 fails Rule A")
    void testIncomeUnderFiftyThousandFails() {
        CreditEvaluationFormDto dto = new CreditEvaluationFormDto(
                "Low Earner",
                new BigDecimal("49999.00"),
                new BigDecimal("100.00"),
                new BigDecimal("400.00"),
                5,
                new BigDecimal("50.00")
        );

        CreditRuleEvaluation result = engine.evaluateAndSave(dto);

        assertFalse(result.isApproved());
        assertTrue(result.getOutcomeState().contains("Income Below Minimum"));
    }

    @Test
    @DisplayName("Division by zero guard: Income of $0 safely rejected at Rule A")
    void testZeroIncomeDivisionGuard() {
        CreditEvaluationFormDto dto = new CreditEvaluationFormDto(
                "Zero Earner",
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                new BigDecimal("400.00"),
                2,
                new BigDecimal("50.00")
        );

        CreditRuleEvaluation result = engine.evaluateAndSave(dto);

        assertFalse(result.isApproved());
        assertTrue(result.getOutcomeState().contains("Income Below Minimum"));
    }

    @Test
    @DisplayName("Boundary: Car payment ratio of exactly 10% fails Rule B (Must be strictly < 10%)")
    void testBoundaryCarPaymentRatioExactlyTenPercentFails() {
        // Income = 60,000; Monthly car = 500 => Annual car = 6,000 => exactly 10.0%
        CreditEvaluationFormDto dto = new CreditEvaluationFormDto(
                "Car Heavy",
                new BigDecimal("60000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("800.00"),
                5,
                new BigDecimal("100.00")
        );

        CreditRuleEvaluation result = engine.evaluateAndSave(dto);

        assertFalse(result.isApproved());
        assertTrue(result.getOutcomeState().contains("Excessive Car Debt"));
        assertTrue(result.getRuleFiringLog().contains("RULE_B_TO_C"));
        assertTrue(result.getRuleFiringLog().contains("Rule Failed"));
    }

    @Test
    @DisplayName("Boundary: Mortgage payment ratio of exactly 20% fails Rule C (Must be strictly < 20%)")
    void testBoundaryMortgagePaymentRatioExactlyTwentyPercentFails() {
        // Income = 60,000; Car = 400 (8% < 10%); Mortgage = 1000 => Annual mortgage = 12,000 => exactly 20.0%
        CreditEvaluationFormDto dto = new CreditEvaluationFormDto(
                "Mortgage Heavy",
                new BigDecimal("60000.00"),
                new BigDecimal("400.00"),
                new BigDecimal("1000.00"),
                5,
                new BigDecimal("100.00")
        );

        CreditRuleEvaluation result = engine.evaluateAndSave(dto);

        assertFalse(result.isApproved());
        assertTrue(result.getOutcomeState().contains("Excessive Housing Debt"));
        assertTrue(result.getRuleFiringLog().contains("RULE_C_TO_D"));
        assertTrue(result.getRuleFiringLog().contains("Rule Failed"));
    }

    @Test
    @DisplayName("Boundary: Tenure of exactly 4 years approves at State F ($10,000 limit)")
    void testBoundaryTenureExactlyFourYearsApprovesStateF() {
        CreditEvaluationFormDto dto = new CreditEvaluationFormDto(
                "Tenured Staff",
                new BigDecimal("70000.00"),
                new BigDecimal("350.00"), // 6%
                new BigDecimal("1000.00"), // 17.1%
                4, // Exactly 4 years
                new BigDecimal("500.00")
        );

        CreditRuleEvaluation result = engine.evaluateAndSave(dto);

        assertTrue(result.isApproved());
        assertTrue(result.getOutcomeState().contains("STATE F"));
        assertEquals(new BigDecimal("10000.00"), result.getGrantedCreditLimit());
        assertTrue(result.getRuleFiringLog().contains("STATE F"));
    }

    @Test
    @DisplayName("Short tenure (<4 yrs) with secondary debt < 5% approves at State F ($10,000 limit)")
    void testShortTenureLowDebtApprovesStateF() {
        // Income = 60,000; Car = 400; Mortgage = 900; Tenure = 2 yrs (<4); Other debt = 200/mo => 2400/yr => 4.0% (<5%)
        CreditEvaluationFormDto dto = new CreditEvaluationFormDto(
                "New Hire Low Debt",
                new BigDecimal("60000.00"),
                new BigDecimal("400.00"),
                new BigDecimal("900.00"),
                2,
                new BigDecimal("200.00")
        );

        CreditRuleEvaluation result = engine.evaluateAndSave(dto);

        assertTrue(result.isApproved());
        assertTrue(result.getOutcomeState().contains("STATE F"));
        assertEquals(new BigDecimal("10000.00"), result.getGrantedCreditLimit());
        assertTrue(result.getRuleFiringLog().contains("RULE_H_TO_F_I"));
        assertTrue(result.getRuleFiringLog().contains("STATE F"));
    }

    @Test
    @DisplayName("Boundary: Short tenure with secondary debt of exactly 5% triggers State I restricted ($3,000 limit)")
    void testBoundaryShortTenureDebtExactlyFivePercentTriggersStateI() {
        // Income = 60,000; Car = 400; Mortgage = 900; Tenure = 2 yrs; Other debt = 250/mo => 3000/yr => exactly 5.0%
        CreditEvaluationFormDto dto = new CreditEvaluationFormDto(
                "New Hire Five Percent Debt",
                new BigDecimal("60000.00"),
                new BigDecimal("400.00"),
                new BigDecimal("900.00"),
                2,
                new BigDecimal("250.00")
        );

        CreditRuleEvaluation result = engine.evaluateAndSave(dto);

        assertTrue(result.isApproved());
        assertTrue(result.getOutcomeState().contains("STATE I"));
        assertEquals(new BigDecimal("3000.00"), result.getGrantedCreditLimit());
        assertTrue(result.getRuleFiringLog().contains("STATE I"));
    }

    @Test
    @DisplayName("Short tenure with secondary debt > 5% triggers State I restricted ($3,000 limit)")
    void testShortTenureHighDebtTriggersStateI() {
        // Income = 60,000; Car = 400; Mortgage = 900; Tenure = 1 yr; Other debt = 350/mo => 4200/yr => 7.0% (>5%)
        CreditEvaluationFormDto dto = new CreditEvaluationFormDto(
                "New Hire High Debt",
                new BigDecimal("60000.00"),
                new BigDecimal("400.00"),
                new BigDecimal("900.00"),
                1,
                new BigDecimal("350.00")
        );

        CreditRuleEvaluation result = engine.evaluateAndSave(dto);

        assertTrue(result.isApproved());
        assertTrue(result.getOutcomeState().contains("STATE I"));
        assertEquals(new BigDecimal("3000.00"), result.getGrantedCreditLimit());
    }

    @Test
    @DisplayName("CSV Export outputs valid header and evaluation rows")
    void testCsvExportFormat() {
        String csv = engine.exportEvaluationsCsv();
        assertNotNull(csv);
        assertTrue(csv.contains("Timestamp,Applicant"));
    }
}
