package com.stratops.stratops360.rule;

import java.math.BigDecimal;

/**
 * Slide 9: Step A -> B
 * "If INC > 50,000 Ask about car payments, Else EXIT"
 */
public class IncomeThresholdRule implements CreditRule {

    @Override
    public String getRuleCode() {
        return "RULE_A_TO_B";
    }

    @Override
    public String getRuleName() {
        return "Minimum Income Qualification";
    }

    @Override
    public boolean evaluate(EvaluationContext context) {
        BigDecimal income = context.getAnnualIncome();
        BigDecimal minThreshold = context.getThresholds().getMinIncome();

        boolean satisfied = income.compareTo(minThreshold) > 0;

        if (satisfied) {
            context.setCurrentStage("STATE_B");
            context.addStep(new InferenceStep(
                    getRuleCode(), getRuleName(),
                    "Evaluating Condition: Annual Income > Minimum Benchmark",
                    "$" + income,
                    "> $" + minThreshold,
                    true, false,
                    "STATE_B",
                    "Income exceeds $" + minThreshold + ". Transitioning to State B (Query Car Payments)."
            ));
            return true;
        } else {
            context.setTerminal(true);
            context.setApproved(false);
            context.setGrantedCreditLimit(BigDecimal.ZERO);
            context.setOutcomeState("EXIT: Income Below Minimum ($" + minThreshold + ")");
            context.setCurrentStage("EXIT_A");
            context.addStep(new InferenceStep(
                    getRuleCode(), getRuleName(),
                    "Evaluating Condition: Annual Income > Minimum Benchmark",
                    "$" + income,
                    "> $" + minThreshold,
                    false, true,
                    "EXIT_A",
                    "Income fails to satisfy minimum threshold of $" + minThreshold + ". Application Terminated."
            ));
            return false;
        }
    }
}
