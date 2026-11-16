package com.stratops.stratops360.rule;

import java.math.BigDecimal;

/**
 * Slide 9: Step D -> E -> F / G
 * "If years >= 4, Grant 10,000 line (State F), Else do G"
 */
public class EmploymentTenureRule implements CreditRule {

    @Override
    public String getRuleCode() {
        return "RULE_E_TO_F_G";
    }

    @Override
    public String getRuleName() {
        return "Employment Tenure Evaluation";
    }

    @Override
    public boolean evaluate(EvaluationContext context) {
        if (context.isTerminal() || !"STATE_D".equals(context.getCurrentStage())) {
            return false;
        }

        int tenure = context.getYearsEmployed();
        int minTenure = context.getThresholds().getMinTenureYears();

        if (tenure >= minTenure) {
            BigDecimal limit = context.getThresholds().getTier1Limit();
            context.setApproved(true);
            context.setGrantedCreditLimit(limit);
            context.setOutcomeState("STATE F: Approved ($" + limit + " Limit)");
            context.setCurrentStage("STATE_F");
            context.setTerminal(true);

            context.addStep(new InferenceStep(
                    getRuleCode(), getRuleName(),
                    "Evaluating Condition: Employment Tenure >= Minimum Stability Benchmark",
                    tenure + " years",
                    ">= " + minTenure + " years",
                    true, true,
                    "STATE_F",
                    "Employment tenure (" + tenure + " yrs) satisfies senior threshold. State F Triggered: Tier-1 Credit Line Granted ($" + limit + ")."
            ));
            return true;
        } else {
            // Branch to G
            context.setCurrentStage("STATE_G");
            context.addStep(new InferenceStep(
                    getRuleCode(), getRuleName(),
                    "Evaluating Condition: Employment Tenure >= Minimum Stability Benchmark",
                    tenure + " years",
                    ">= " + minTenure + " years",
                    false, false,
                    "STATE_G",
                    "Tenure (" + tenure + " yrs) is under " + minTenure + " years. Branching to State G: Query secondary debts to determine risk mitigation."
            ));
            return true;
        }
    }
}
