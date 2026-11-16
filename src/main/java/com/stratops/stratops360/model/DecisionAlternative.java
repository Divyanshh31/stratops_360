package com.stratops.stratops360.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "decision_alternatives")
public class DecisionAlternative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1500)
    private String description;

    @Column(length = 500)
    private String pros;

    @Column(length = 500)
    private String cons;

    private Integer riskScore = 5; // 1 (Low) to 10 (High)

    private Double projectedBenefitPercentage = 0.0;

    private Double estimatedCost = 0.0;

    private boolean selected = false;

    public DecisionAlternative() {
    }

    public DecisionAlternative(String title, String description, String pros, String cons,
                               Integer riskScore, Double projectedBenefitPercentage, Double estimatedCost, boolean selected) {
        this.title = title;
        this.description = description;
        this.pros = pros;
        this.cons = cons;
        this.riskScore = riskScore != null ? riskScore : 5;
        this.projectedBenefitPercentage = projectedBenefitPercentage != null ? projectedBenefitPercentage : 0.0;
        this.estimatedCost = estimatedCost != null ? estimatedCost : 0.0;
        this.selected = selected;
    }

    /**
     * Calculates the Risk-Adjusted Return ratio (ROI / Risk).
     */
    public double getRiskAdjustedScore() {
        if (riskScore == null || riskScore <= 0) return 0.0;
        double benefit = projectedBenefitPercentage != null ? projectedBenefitPercentage : 0.0;
        return Math.round((benefit / riskScore) * 100.0) / 100.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecisionAlternative that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
