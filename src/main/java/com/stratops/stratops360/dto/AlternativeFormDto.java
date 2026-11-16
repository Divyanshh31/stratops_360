package com.stratops.stratops360.dto;

import jakarta.validation.constraints.*;

public class AlternativeFormDto {

    @NotNull(message = "Initiative ID is required")
    private Long initiativeId;

    @NotBlank(message = "Alternative title is required")
    @Size(max = 200, message = "Title must be 200 characters or fewer")
    private String title;

    @NotBlank(message = "Description of solution is required")
    @Size(max = 1500, message = "Description must be 1500 characters or fewer")
    private String description;

    @NotBlank(message = "Pros / strategic advantages are required")
    @Size(max = 500, message = "Pros must be 500 characters or fewer")
    private String pros;

    @NotBlank(message = "Cons / constraints are required")
    @Size(max = 500, message = "Cons must be 500 characters or fewer")
    private String cons;

    @NotNull(message = "Risk score is required")
    @Min(value = 1, message = "Risk score must be at least 1 (Low)")
    @Max(value = 10, message = "Risk score must be at most 10 (High)")
    private Integer riskScore = 4;

    @NotNull(message = "Projected benefit % is required")
    private Double projectedBenefitPercentage = 15.0;

    @NotNull(message = "Estimated cost is required")
    @PositiveOrZero(message = "Cost must be zero or positive")
    private Double estimatedCost = 50000.0;

    public AlternativeFormDto() {
    }

    public Long getInitiativeId() {
        return initiativeId;
    }

    public void setInitiativeId(Long initiativeId) {
        this.initiativeId = initiativeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPros() {
        return pros;
    }

    public void setPros(String pros) {
        this.pros = pros;
    }

    public String getCons() {
        return cons;
    }

    public void setCons(String cons) {
        this.cons = cons;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public Double getProjectedBenefitPercentage() {
        return projectedBenefitPercentage;
    }

    public void setProjectedBenefitPercentage(Double projectedBenefitPercentage) {
        this.projectedBenefitPercentage = projectedBenefitPercentage;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
}
