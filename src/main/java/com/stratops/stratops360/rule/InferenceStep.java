package com.stratops.stratops360.rule;

public class InferenceStep {

    private final String ruleCode;
    private final String ruleName;
    private final String conditionDescription;
    private final String evaluatedValue;
    private final String thresholdCriteria;
    private final boolean satisfied;
    private final boolean terminal;
    private final String resultingState;
    private final String explanation;

    public InferenceStep(String ruleCode, String ruleName, String conditionDescription,
                         String evaluatedValue, String thresholdCriteria, boolean satisfied,
                         boolean terminal, String resultingState, String explanation) {
        this.ruleCode = ruleCode;
        this.ruleName = ruleName;
        this.conditionDescription = conditionDescription;
        this.evaluatedValue = evaluatedValue;
        this.thresholdCriteria = thresholdCriteria;
        this.satisfied = satisfied;
        this.terminal = terminal;
        this.resultingState = resultingState;
        this.explanation = explanation;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getConditionDescription() {
        return conditionDescription;
    }

    public String getEvaluatedValue() {
        return evaluatedValue;
    }

    public String getThresholdCriteria() {
        return thresholdCriteria;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public String getResultingState() {
        return resultingState;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public String toString() {
        return String.format("[%s - %s]: %s | Evaluated: %s vs Criteria: %s -> %s (State: %s)",
                ruleCode, ruleName, conditionDescription, evaluatedValue, thresholdCriteria,
                (satisfied ? "SATISFIED" : "FAILED"), resultingState);
    }
}
