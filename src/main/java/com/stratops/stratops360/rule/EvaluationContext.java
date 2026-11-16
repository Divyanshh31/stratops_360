package com.stratops.stratops360.rule;

import com.stratops.stratops360.model.RuleThresholdConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates facts gathered about the applicant and the state transitions of the forward-chaining inference engine.
 */
public class EvaluationContext {

    private final String applicantName;
    private final BigDecimal annualIncome;
    private final BigDecimal monthlyCarPayment;
    private final BigDecimal monthlyMortgagePayment;
    private final int yearsEmployed;
    private final BigDecimal monthlyOtherDebt;
    private final RuleThresholdConfig thresholds;

    private boolean approved = false;
    private BigDecimal grantedCreditLimit = BigDecimal.ZERO;
    private String outcomeState = "PENDING";
    private boolean terminal = false;
    private String currentStage = "STATE_A";

    private final List<InferenceStep> steps = new ArrayList<>();

    public EvaluationContext(String applicantName, BigDecimal annualIncome, BigDecimal monthlyCarPayment,
                             BigDecimal monthlyMortgagePayment, int yearsEmployed, BigDecimal monthlyOtherDebt,
                             RuleThresholdConfig thresholds) {
        this.applicantName = applicantName;
        this.annualIncome = annualIncome != null ? annualIncome : BigDecimal.ZERO;
        this.monthlyCarPayment = monthlyCarPayment != null ? monthlyCarPayment : BigDecimal.ZERO;
        this.monthlyMortgagePayment = monthlyMortgagePayment != null ? monthlyMortgagePayment : BigDecimal.ZERO;
        this.yearsEmployed = Math.max(0, yearsEmployed);
        this.monthlyOtherDebt = monthlyOtherDebt != null ? monthlyOtherDebt : BigDecimal.ZERO;
        this.thresholds = thresholds;
    }

    public void addStep(InferenceStep step) {
        steps.add(step);
    }

    /**
     * Computes the annual payment for a given monthly obligation.
     */
    public BigDecimal calculateAnnual(BigDecimal monthly) {
        if (monthly == null) return BigDecimal.ZERO;
        return monthly.multiply(BigDecimal.valueOf(12));
    }

    /**
     * Calculates percentage ratio of obligation relative to annual income:
     * (obligation / income) * 100
     * Returns 100.0 if income <= 0 to avoid division by zero.
     */
    public BigDecimal calculateRatio(BigDecimal annualObligation) {
        if (annualIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.valueOf(100.0);
        }
        return annualObligation.multiply(BigDecimal.valueOf(100))
                .divide(annualIncome, 2, RoundingMode.HALF_UP);
    }

    public String generateFormattedTrace() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INFERENCE ENGINE START: Forward-Chaining Rule Evaluation ===\n");
        sb.append(String.format("Initial Facts Gathered: Applicant='%s', Income=$%,.2f, CarPayment=$%,.2f/mo, Mortgage=$%,.2f/mo, YearsEmployed=%d, OtherDebt=$%,.2f/mo\n\n",
                applicantName, annualIncome, monthlyCarPayment, monthlyMortgagePayment, yearsEmployed, monthlyOtherDebt));

        for (InferenceStep step : steps) {
            sb.append(String.format("[%s]: %s\n", step.getRuleCode(), step.getConditionDescription()));
            sb.append(String.format("  Evaluated: %s (Threshold Criteria: %s)\n", step.getEvaluatedValue(), step.getThresholdCriteria()));
            if (step.isSatisfied()) {
                sb.append(String.format("  ✅ Rule Satisfied -> %s\n", step.getExplanation()));
            } else {
                sb.append(String.format("  ❌ Rule Failed -> %s\n", step.getExplanation()));
            }
            if (step.isTerminal()) {
                sb.append(String.format("  🎯 Final Conclusion: %s\n", step.getResultingState()));
            }
            sb.append("\n");
        }

        sb.append("=== INFERENCE ENGINE RESULT: ").append(outcomeState).append(" ===");
        return sb.toString();
    }

    // Getters and Setters
    public String getApplicantName() {
        return applicantName;
    }

    public BigDecimal getAnnualIncome() {
        return annualIncome;
    }

    public BigDecimal getMonthlyCarPayment() {
        return monthlyCarPayment;
    }

    public BigDecimal getMonthlyMortgagePayment() {
        return monthlyMortgagePayment;
    }

    public int getYearsEmployed() {
        return yearsEmployed;
    }

    public BigDecimal getMonthlyOtherDebt() {
        return monthlyOtherDebt;
    }

    public RuleThresholdConfig getThresholds() {
        return thresholds;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public BigDecimal getGrantedCreditLimit() {
        return grantedCreditLimit;
    }

    public void setGrantedCreditLimit(BigDecimal grantedCreditLimit) {
        this.grantedCreditLimit = grantedCreditLimit;
    }

    public String getOutcomeState() {
        return outcomeState;
    }

    public void setOutcomeState(String outcomeState) {
        this.outcomeState = outcomeState;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public List<InferenceStep> getSteps() {
        return steps;
    }
}
