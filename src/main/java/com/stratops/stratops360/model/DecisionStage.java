package com.stratops.stratops360.model;

public enum DecisionStage {
    INTELLIGENCE("1. Intelligence", "Problem Discovery: What is the organizational discrepancy or problem?", "1"),
    DESIGN("2. Design", "Solution Discovery: What are the possible solution alternatives?", "2"),
    CHOICE("3. Choice", "Choosing Solutions: What is the best solution based on ROI and risk?", "3"),
    IMPLEMENTATION("4. Implementation", "Solution Testing & Feedback: Is the solution working? How to refine?", "4");

    private final String stageName;
    private final String description;
    private final String stepNumber;

    DecisionStage(String stageName, String description, String stepNumber) {
        this.stageName = stageName;
        this.description = description;
        this.stepNumber = stepNumber;
    }

    public String getStageName() {
        return stageName;
    }

    public String getDisplayName() {
        return stageName;
    }

    public String getDescription() {
        return description;
    }

    public String getStepNumber() {
        return stepNumber;
    }
}
