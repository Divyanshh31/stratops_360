package com.stratops.stratops360.rule;

import java.math.BigDecimal;

/**
 * Slide 9: Step C -> D
 * "If mortgage payment < 20% of income, Grant credit line (State D), Else EXIT"
 */
public class MortgagePaymentRatioRule implements CreditRule {

    @Override
    public String getRuleCode() {
        return "RULE_C_TO_D";
    }

    @Override
    public String getRuleName() {
        return "Housing Debt-to-Income Evaluation";
    }

    @Override
    public boolean evaluate(EvaluationContext context) {
        if (context.isTerminal() || !"STATE_C".equals(context.getCurrentStage())) {
            return false;
        }

        BigDecimal annualMortgage = context.calculateAnnual(context.getMonthlyMortgagePayment());
        BigDecimal ratio = context.calculateRatio(annualMortgage);
        BigDecimal maxRatio = context.getThresholds().getMaxMortgageRatio();

        boolean satisfied = ratio.compareTo(maxRatio) < 0;

        if (satisfied) {
            context.setCurrentStage("STATE_D");
            context.addStep(new InferenceStep(
                    getRuleCode(), getRuleName(),
                    "Evaluating Condition: Annual Housing/Mortgage Payments < Max Ratio Ceiling",
                    ratio + "% ($" + annualMortgage + "/yr)",
                    "< " + maxRatio + "%",
                    true, false,
                    "STATE_D",
                    "Housing debt obligation of " + ratio + "% is within allowable ceiling (< " + maxRatio + "%). State D Achieved: Baseline Credit Line Qualification Granted!"
            ));
            return true;
        } else {
            context.setTerminal(true);
            context.setApproved(false);
            context.setGrantedCreditLimit(BigDecimal.ZERO);
            context.setOutcomeState("EXIT: Excessive Housing Debt (>" + maxRatio + "%)");
            context.setCurrentStage("EXIT_C");
            context.addStep(new InferenceStep(
                    getRuleCode(), getRuleName(),
                    "Evaluating Condition: Annual Housing/Mortgage Payments < Max Ratio Ceiling",
                    ratio + "% ($" + annualMortgage + "/yr)",
                    "< " + maxRatio + "%",
                    false, true,
                    "EXIT_C",
                    "Housing debt ratio of " + ratio + "% exceeds maximum permissible ceiling of " + maxRatio + "%. Application Terminated."
            ));
            return false;
        }
    }
}
