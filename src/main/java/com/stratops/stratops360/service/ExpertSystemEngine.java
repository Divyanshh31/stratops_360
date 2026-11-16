package com.stratops.stratops360.service;

import com.stratops.stratops360.config.ExpertSystemProperties;
import com.stratops.stratops360.dto.CreditEvaluationFormDto;
import com.stratops.stratops360.dto.RuleThresholdConfigFormDto;
import com.stratops.stratops360.model.CreditRuleEvaluation;
import com.stratops.stratops360.model.RuleThresholdConfig;
import com.stratops.stratops360.repository.CreditRuleEvaluationRepository;
import com.stratops.stratops360.repository.RuleThresholdConfigRepository;
import com.stratops.stratops360.rule.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Forward-Chaining Inference Engine implementation for Slide 8 & 9.
 * Traverses an ordered rule sequence matching applicant facts against the Knowledge Base.
 */
@Service
@Transactional
public class ExpertSystemEngine {

    private final CreditRuleEvaluationRepository evaluationRepository;
    private final RuleThresholdConfigRepository thresholdConfigRepository;
    private final ExpertSystemProperties defaultProperties;

    private final List<CreditRule> ruleSequence;

    public ExpertSystemEngine(CreditRuleEvaluationRepository evaluationRepository,
                              RuleThresholdConfigRepository thresholdConfigRepository,
                              ExpertSystemProperties defaultProperties) {
        this.evaluationRepository = evaluationRepository;
        this.thresholdConfigRepository = thresholdConfigRepository;
        this.defaultProperties = defaultProperties;

        // Structured forward-chaining rule pipeline matching Slide 9 topology
        this.ruleSequence = List.of(
                new IncomeThresholdRule(),       // Step A -> B
                new CarPaymentRatioRule(),        // Step B -> C
                new MortgagePaymentRatioRule(),   // Step C -> D
                new EmploymentTenureRule(),       // Step D -> E -> F or G
                new SecondaryDebtRatioRule()      // Step G -> H -> F or I
        );
    }

    @Transactional(readOnly = true)
    @Cacheable("ruleThresholds")
    public RuleThresholdConfig getThresholdConfig() {
        return thresholdConfigRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> {
                    RuleThresholdConfig initial = new RuleThresholdConfig(
                            defaultProperties.getMinIncome(),
                            defaultProperties.getMaxCarRatio(),
                            defaultProperties.getMaxMortgageRatio(),
                            defaultProperties.getMinTenureYears(),
                            defaultProperties.getMaxOtherDebtRatio(),
                            defaultProperties.getTier1Limit(),
                            defaultProperties.getTier2Limit(),
                            "System Defaults"
                    );
                    return thresholdConfigRepository.save(initial);
                });
    }

    @CacheEvict(value = "ruleThresholds", allEntries = true)
    public RuleThresholdConfig updateThresholdConfig(RuleThresholdConfigFormDto dto, String updatedBy) {
        RuleThresholdConfig config = getThresholdConfig();
        config.setMinIncome(dto.getMinIncome());
        config.setMaxCarRatio(dto.getMaxCarRatio());
        config.setMaxMortgageRatio(dto.getMaxMortgageRatio());
        config.setMinTenureYears(dto.getMinTenureYears());
        config.setMaxOtherDebtRatio(dto.getMaxOtherDebtRatio());
        config.setTier1Limit(dto.getTier1Limit());
        config.setTier2Limit(dto.getTier2Limit());
        config.setUpdatedBy(updatedBy != null ? updatedBy : "Administrator");
        config.setUpdatedAt(java.time.LocalDateTime.now());
        return thresholdConfigRepository.save(config);
    }

    @Transactional(readOnly = true)
    public List<CreditRuleEvaluation> getAllEvaluations() {
        return evaluationRepository.findAllByOrderByEvaluationTimestampDesc();
    }

    @Transactional(readOnly = true)
    public Page<CreditRuleEvaluation> getEvaluationsPaged(Pageable pageable) {
        return evaluationRepository.findAllByOrderByEvaluationTimestampDesc(pageable);
    }

    /**
     * Executes the Forward-Chaining Inference Engine across the configured rules.
     */
    public CreditRuleEvaluation evaluateAndSave(CreditEvaluationFormDto dto) {
        RuleThresholdConfig thresholds = getThresholdConfig();

        EvaluationContext context = new EvaluationContext(
                dto.getApplicantName().trim(),
                dto.getAnnualIncome(),
                dto.getMonthlyCarPayment(),
                dto.getMonthlyMortgagePayment(),
                dto.getYearsEmployed(),
                dto.getMonthlyOtherDebt(),
                thresholds
        );

        // Forward chaining loop: evaluates rules until terminal state reached
        for (CreditRule rule : ruleSequence) {
            rule.evaluate(context);
            if (context.isTerminal()) {
                break;
            }
        }

        String traceLog = context.generateFormattedTrace();

        CreditRuleEvaluation evaluation = new CreditRuleEvaluation(
                context.getApplicantName(),
                context.getAnnualIncome(),
                context.getMonthlyCarPayment(),
                context.getMonthlyMortgagePayment(),
                context.getYearsEmployed(),
                context.getMonthlyOtherDebt(),
                context.isApproved(),
                context.getGrantedCreditLimit(),
                context.getOutcomeState(),
                traceLog
        );

        return evaluationRepository.save(evaluation);
    }

    /**
     * Generates CSV export for general control compliance auditing.
     */
    @Transactional(readOnly = true)
    public String exportEvaluationsCsv() {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Timestamp,Applicant,Annual Income,Monthly Car,Monthly Mortgage,Tenure (Years),Other Debt,Approved,Granted Limit,Outcome State\n");
        for (CreditRuleEvaluation e : getAllEvaluations()) {
            csv.append(String.format("%d,%s,\"%s\",%.2f,%.2f,%.2f,%d,%.2f,%b,%.2f,\"%s\"\n",
                    e.getId(),
                    e.getEvaluationTimestamp(),
                    e.getApplicantName().replace("\"", "\"\""),
                    e.getAnnualIncome().doubleValue(),
                    e.getMonthlyCarPayment().doubleValue(),
                    e.getMonthlyMortgagePayment().doubleValue(),
                    e.getYearsEmployed(),
                    e.getMonthlyOtherDebt().doubleValue(),
                    e.isApproved(),
                    e.getGrantedCreditLimit().doubleValue(),
                    e.getOutcomeState()));
        }
        return csv.toString();
    }
}
