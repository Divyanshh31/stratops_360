package com.stratops.stratops360.service;

import com.stratops.stratops360.dto.AlternativeFormDto;
import com.stratops.stratops360.dto.DecisionInitiativeFormDto;
import com.stratops.stratops360.exception.InvalidDecisionTransitionException;
import com.stratops.stratops360.exception.ResourceNotFoundException;
import com.stratops.stratops360.model.*;
import com.stratops.stratops360.repository.DecisionInitiativeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements Herbert Simon's 4-Stage Decision-Making Model (Slide 11):
 * 1. Intelligence (Problem Discovery)
 * 2. Design (Developing Alternatives)
 * 3. Choice (Selecting Alternative)
 * 4. Implementation (Testing & Feedback Loop)
 */
@Service
@Transactional
public class DecisionService {

    private final DecisionInitiativeRepository initiativeRepository;

    public DecisionService(DecisionInitiativeRepository initiativeRepository) {
        this.initiativeRepository = initiativeRepository;
    }

    @Transactional(readOnly = true)
    public List<DecisionInitiative> getAllInitiatives() {
        return initiativeRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<DecisionInitiative> getInitiativesByStage(DecisionStage stage) {
        return initiativeRepository.findByCurrentStage(stage);
    }

    @Transactional(readOnly = true)
    public List<DecisionInitiative> getInitiativesByPerspective(KpiPerspective perspective) {
        return initiativeRepository.findByPerspective(perspective);
    }

    @Transactional(readOnly = true)
    public DecisionInitiative getInitiativeById(Long id) {
        return initiativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Decision initiative not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<DecisionInitiative> searchInitiatives(String query) {
        if (query == null || query.isBlank()) {
            return getAllInitiatives();
        }
        return initiativeRepository.findByTitleContainingIgnoreCaseOrProblemStatementContainingIgnoreCase(query, query);
    }

    public DecisionInitiative createInitiative(DecisionInitiativeFormDto dto) {
        DecisionInitiative init = new DecisionInitiative(
                dto.getTitle().trim(),
                dto.getProblemStatement().trim(),
                dto.getPerspective(),
                dto.getRelatedKpiCode() != null ? dto.getRelatedKpiCode().trim() : null,
                DecisionStage.INTELLIGENCE,
                dto.getEstimatedCost() != null ? dto.getEstimatedCost() : 0.0,
                0.0,
                dto.getOwner().trim(),
                "DISCOVERY"
        );
        return initiativeRepository.save(init);
    }

    public DecisionInitiative addAlternative(AlternativeFormDto dto) {
        DecisionInitiative initiative = getInitiativeById(dto.getInitiativeId());

        DecisionAlternative alt = new DecisionAlternative(
                dto.getTitle().trim(),
                dto.getDescription().trim(),
                dto.getPros().trim(),
                dto.getCons().trim(),
                dto.getRiskScore(),
                dto.getProjectedBenefitPercentage(),
                dto.getEstimatedCost(),
                false
        );

        initiative.addAlternative(alt);
        initiative.setStatus("DESIGNING_ALTERNATIVES");
        return initiativeRepository.save(initiative);
    }

    public DecisionInitiative selectAlternative(Long initiativeId, Long alternativeId) {
        DecisionInitiative init = getInitiativeById(initiativeId);

        boolean found = false;
        for (DecisionAlternative alt : init.getAlternatives()) {
            if (alt.getId().equals(alternativeId)) {
                alt.setSelected(true);
                init.setEstimatedCost(alt.getEstimatedCost());
                init.setProjectedRoi(alt.getProjectedBenefitPercentage());
                found = true;
            } else {
                alt.setSelected(false);
            }
        }

        if (!found) {
            throw new ResourceNotFoundException("Alternative not found with ID: " + alternativeId + " under initiative: " + initiativeId);
        }

        init.setStatus("CHOICE_APPROVED");
        return initiativeRepository.save(init);
    }

    /**
     * Enforces Herbert Simon's academic stage validation rules before advancing:
     * - Cannot advance to Choice without at least 2 modeled alternatives.
     * - Cannot advance to Implementation without an approved selected choice.
     * - Cannot complete without recorded feedback.
     */
    public DecisionInitiative advanceStage(Long initiativeId) {
        DecisionInitiative init = getInitiativeById(initiativeId);

        switch (init.getCurrentStage()) {
            case INTELLIGENCE:
                if (init.getProblemStatement() == null || init.getProblemStatement().isBlank()) {
                    throw new InvalidDecisionTransitionException("Stage 1 (Intelligence) requires a documented problem discovery statement before proceeding to Design.");
                }
                init.setCurrentStage(DecisionStage.DESIGN);
                init.setStatus("DESIGNING_ALTERNATIVES");
                break;

            case DESIGN:
                if (init.getAlternatives() == null || init.getAlternatives().size() < 2) {
                    throw new InvalidDecisionTransitionException(
                            "Herbert Simon Model requires modeling at least two competing solution alternatives in Stage 2 (Design) before advancing to Choice. Current count: " +
                                    (init.getAlternatives() != null ? init.getAlternatives().size() : 0));
                }
                init.setCurrentStage(DecisionStage.CHOICE);
                init.setStatus("PENDING_CHOICE");
                break;

            case CHOICE:
                if (init.getSelectedAlternative() == null) {
                    throw new InvalidDecisionTransitionException(
                            "Stage 3 (Choice) requires selecting and approving one preferred solution alternative before committing capex to Implementation.");
                }
                init.setCurrentStage(DecisionStage.IMPLEMENTATION);
                init.setStatus("IN_EXECUTION");
                break;

            case IMPLEMENTATION:
                if (init.getImplementationFeedback() == null || init.getImplementationFeedback().isBlank()) {
                    throw new InvalidDecisionTransitionException(
                            "Stage 4 (Implementation) requires documenting feedback loop telemetry (confirming whether the solution resolved the initial KPI discrepancy) before closing.");
                }
                init.setStatus("COMPLETED");
                break;
        }

        return initiativeRepository.save(init);
    }

    public DecisionInitiative updateFeedback(Long id, String feedback) {
        DecisionInitiative init = getInitiativeById(id);
        init.setImplementationFeedback(feedback);
        return initiativeRepository.save(init);
    }
}
