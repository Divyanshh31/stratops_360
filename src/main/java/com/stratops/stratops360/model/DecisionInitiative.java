package com.stratops.stratops360.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "decision_initiatives", indexes = {
        @Index(name = "idx_init_stage", columnList = "currentStage"),
        @Index(name = "idx_init_perspective", columnList = "perspective"),
        @Index(name = "idx_init_created", columnList = "createdAt")
})
public class DecisionInitiative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String problemStatement; // Stage 1: Intelligence

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KpiPerspective perspective;

    private String relatedKpiCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecisionStage currentStage = DecisionStage.INTELLIGENCE;

    private Double estimatedCost = 0.0;

    private Double projectedRoi = 0.0;

    private String owner;

    private String status = "IN_PROGRESS"; // DISCOVERY, DESIGNING_ALTERNATIVES, PENDING_CHOICE, IN_EXECUTION, COMPLETED

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastStageTransitionAt = LocalDateTime.now();

    @Column(length = 2000)
    private String implementationFeedback; // Stage 4: Implementation Feedback loop

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "initiative_id")
    private List<DecisionAlternative> alternatives = new ArrayList<>();

    public DecisionInitiative() {
    }

    public DecisionInitiative(String title, String problemStatement, KpiPerspective perspective,
                              String relatedKpiCode, DecisionStage currentStage, Double estimatedCost,
                              Double projectedRoi, String owner, String status) {
        this.title = title;
        this.problemStatement = problemStatement;
        this.perspective = perspective;
        this.relatedKpiCode = relatedKpiCode;
        this.currentStage = currentStage;
        this.estimatedCost = estimatedCost != null ? estimatedCost : 0.0;
        this.projectedRoi = projectedRoi != null ? projectedRoi : 0.0;
        this.owner = owner;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.lastStageTransitionAt = LocalDateTime.now();
    }

    public void addAlternative(DecisionAlternative alt) {
        alternatives.add(alt);
    }

    public DecisionAlternative getSelectedAlternative() {
        for (DecisionAlternative alt : alternatives) {
            if (alt.isSelected()) {
                return alt;
            }
        }
        return null;
    }

    public String getSelectedAlternativeTitle() {
        DecisionAlternative selected = getSelectedAlternative();
        return selected != null ? selected.getTitle() : null;
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

    public DecisionStage getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(DecisionStage currentStage) {
        this.currentStage = currentStage;
        this.lastStageTransitionAt = LocalDateTime.now();
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Double getProjectedRoi() {
        return projectedRoi;
    }

    public void setProjectedRoi(Double projectedRoi) {
        this.projectedRoi = projectedRoi;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastStageTransitionAt() {
        return lastStageTransitionAt;
    }

    public void setLastStageTransitionAt(LocalDateTime lastStageTransitionAt) {
        this.lastStageTransitionAt = lastStageTransitionAt;
    }

    public String getImplementationFeedback() {
        return implementationFeedback;
    }

    public void setImplementationFeedback(String implementationFeedback) {
        this.implementationFeedback = implementationFeedback;
    }

    public List<DecisionAlternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<DecisionAlternative> alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DecisionInitiative that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
