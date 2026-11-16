package com.stratops.stratops360.rule;

import java.math.BigDecimal;

/**
 * Slide 9: Step G -> H -> F / I
 * "If other debt < 5% of income, Do F (Limit 10,000), Else do I (Limit 3,000)"
 */
public class SecondaryDebtRatioRule implements CreditRule {

    @Override
    public String getRuleCode() {
        return "RULE_H_TO_F_I";
    }

    @Override
    public String getRuleName() {
        return "Secondary Debt Offset Evaluation";
    }

    @Override
    public boolean evaluate(EvaluationContext context) {
        if (context.isTerminal() || !"STATE_G".equals(context.getCurrentStage())) {
            return false;
        }

        BigDecimal annualOtherDebt = context.calculateAnnual(context.getMonthlyOtherDebt());
        BigDecimal ratio = context.calculateRatio(annualOtherDebt);
        BigDecimal maxDebtRatio = context.getThresholds().getMaxOtherDebtRatio();

        boolean lowDebt = ratio.compareTo(maxDebtRatio) < 0;

        if (lowDebt) {
            BigDecimal limit = context.getThresholds().getTier1Limit();
            context.setApproved(true);
            context.setGrantedCreditLimit(limit);
            context.setOutcomeState("STATE F: Approved ($" + limit + " Limit - Debt Compensated)");
            context.setCurrentStage("STATE_F");
            context.setTerminal(true);

            context.addStep(new InferenceStep(
                    getRuleCode(), getRuleName(),
                    "Evaluating Condition: Secondary Unsecured Debt Ratio < Conservative Ceiling",
                    ratio + "% ($" + annualOtherDebt + "/yr)",
                    "< " + maxDebtRatio + "%",
                    true, true,
                    "STATE_F",
                    "Low secondary debt (" + ratio + "%) successfully offsets shorter tenure. State F Triggered: Tier-1 Credit Line Granted ($" + limit + ")."
            ));
            return true;
        } else {
            BigDecimal limit = context.getThresholds().getTier2Limit();
            context.setApproved(true);
            context.setGrantedCreditLimit(limit);
            context.setOutcomeState("STATE I: Approved ($" + limit + " Limit - High Secondary Debt)");
            context.setCurrentStage("STATE_I");
            context.setTerminal(true);

            context.addStep(new InferenceStep(
                    getRuleCode(), getRuleName(),
                    "Evaluating Condition: Secondary Unsecured Debt Ratio < Conservative Ceiling",
                    ratio + "% ($" + annualOtherDebt + "/yr)",
                    "< " + maxDebtRatio + "%",
                    false, true,
                    "STATE_I",
                    "Secondary debt (" + ratio + "%) meets or exceeds " + maxDebtRatio + "%. State I Triggered: Exposure Mitigated via Tier-2 Restricted Limit ($" + limit + ")."
            ));
            return true;
        }
    }
}
