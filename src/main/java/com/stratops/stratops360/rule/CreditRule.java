package com.stratops.stratops360.rule;

/**
 * Standard rule interface for Forward-Chaining Inference Engine.
 */
public interface CreditRule {
    String getRuleCode();
    String getRuleName();
    boolean evaluate(EvaluationContext context);
}
