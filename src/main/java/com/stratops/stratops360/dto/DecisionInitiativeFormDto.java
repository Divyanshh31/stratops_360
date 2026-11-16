package com.stratops.stratops360.dto;

import com.stratops.stratops360.model.KpiPerspective;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class DecisionInitiativeFormDto {

    @NotBlank(message = "Initiative title is required")
    @Size(max = 200, message = "Title must be 200 characters or fewer")
    private String title;

    @NotBlank(message = "Problem discovery statement (Intelligence Stage) is required")
    @Size(max = 2000, message = "Problem statement must be 2000 characters or fewer")
    private String problemStatement;

    @NotNull(message = "Scorecard perspective is required")
    private KpiPerspective perspective;

    private String relatedKpiCode;

    @NotBlank(message = "Decision lead / owner name is required")
    @Size(max = 100, message = "Owner name must be 100 characters or fewer")
    private String owner;

    @PositiveOrZero(message = "Estimated capex must be zero or positive")
    private Double estimatedCost = 0.0;

    public DecisionInitiativeFormDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }

    public KpiPerspective getPerspective() {
        return perspective;
    }

    public void setPerspective(KpiPerspective perspective) {
        this.perspective = perspective;
    }

    public String getRelatedKpiCode() {
        return relatedKpiCode;
    }

    public void setRelatedKpiCode(String relatedKpiCode) {
        this.relatedKpiCode = relatedKpiCode;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
}
