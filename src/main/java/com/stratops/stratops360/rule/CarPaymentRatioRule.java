package com.stratops.stratops360.rule;

import java.math.BigDecimal;

/**
 * Slide 9: Step B -> C
 * "If car payment < 10% of income, Ask about mortgage payment, Else EXIT"
 */
public class CarPaymentRatioRule implements CreditRule {

    @Override
    public String getRuleCode() {
        return "RULE_B_TO_C";
    }

    @Override
    public String getRuleName() {
        return "Car Debt-to-Income Evaluation";
    }

    @Override
    public boolean evaluate(EvaluationContext context) {
        if (context.isTerminal() || !"STATE_B".equals(context.getCurrentStage())) {
            return false;
        }

        BigDecimal annualCar = context.calculateAnnual(context.getMonthlyCarPayment());
        BigDecimal ratio = context.calculateRatio(annualCar);
        BigDecimal maxRatio = context.getThresholds().getMaxCarRatio();

        boolean satisfied = ratio.compareTo(maxRatio) < 0;

        if (satisfied) {
            context.setCurrentStage("STATE_C");
            context.addStep(new InferenceStep(
                    getRuleCode(), getRuleName(),
                    "Evaluating Condition: Annual Car Payments < Max Ratio Ceiling",
                    ratio + "% ($" + annualCar + "/yr)",
                    "< " + maxRatio + "%",
                    true, false,
                    "STATE_C",
                    "Car debt obligation of " + ratio + "% is within allowable ceiling (< " + maxRatio + "%). Transitioning to State C (Query Mortgage)."
            ));
            return true;
        } else {
            context.setTerminal(true);
            context.setApproved(false);
            context.setGrantedCreditLimit(BigDecimal.ZERO);
            context.setOutcomeState("EXIT: Excessive Car Debt (>" + maxRatio + "%)");
            context.setCurrentStage("EXIT_B");
            context.addStep(new InferenceStep(
                    getRuleCode(), getRuleName(),
                    "Evaluating Condition: Annual Car Payments < Max Ratio Ceiling",
                    ratio + "% ($" + annualCar + "/yr)",
                    "< " + maxRatio + "%",
                    false, true,
                    "EXIT_B",
                    "Car debt ratio of " + ratio + "% exceeds maximum permissible ceiling of " + maxRatio + "%. Application Terminated."
            ));
            return false;
        }
    }
}
