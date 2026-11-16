package com.stratops.stratops360.service;

import com.stratops.stratops360.exception.InvalidDecisionTransitionException;
import com.stratops.stratops360.model.*;
import com.stratops.stratops360.repository.DecisionInitiativeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecisionServiceTest {

    @Mock
    private DecisionInitiativeRepository initiativeRepository;

    private DecisionService decisionService;

    @BeforeEach
    void setUp() {
        decisionService = new DecisionService(initiativeRepository);
    }

    @Test
    @DisplayName("Stage 1 (Intelligence) to Stage 2 (Design) transitions smoothly")
    void testStage1ToStage2Advance() {
        DecisionInitiative initiative = new DecisionInitiative(
                "Supply Chain Optimization",
                "High fulfillment cycle time detected",
                KpiPerspective.INTERNAL_PROCESS,
                "PROC-01",
                DecisionStage.INTELLIGENCE,
                50000.0,
                20.0,
                "Logistics Director",
                "IN_PROGRESS"
        );
        initiative.setId(1L);

        when(initiativeRepository.findById(1L)).thenReturn(Optional.of(initiative));
        when(initiativeRepository.save(any(DecisionInitiative.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DecisionInitiative updated = decisionService.advanceStage(1L);

        assertEquals(DecisionStage.DESIGN, updated.getCurrentStage());
        assertEquals("DESIGNING_ALTERNATIVES", updated.getStatus());
    }

    @Test
    @DisplayName("Simon Guard: Advancing from Design to Choice with 0 or 1 alternative throws InvalidDecisionTransitionException")
    void testDesignToChoiceBlockedWithoutMinimumTwoAlternatives() {
        DecisionInitiative initiative = new DecisionInitiative(
                "Supply Chain Optimization",
                "High fulfillment cycle time detected",
                KpiPerspective.INTERNAL_PROCESS,
                "PROC-01",
                DecisionStage.DESIGN,
                50000.0,
                20.0,
                "Logistics Director",
                "IN_PROGRESS"
        );
        initiative.setId(1L);

        // Add only 1 alternative
        DecisionAlternative alt1 = new DecisionAlternative(
                "Single Vendor Option", "Direct vendor EDI", "Fast setup", "Expensive", 4, 15.0, 50000.0, false
        );
        initiative.addAlternative(alt1);

        when(initiativeRepository.findById(1L)).thenReturn(Optional.of(initiative));

        InvalidDecisionTransitionException ex = assertThrows(InvalidDecisionTransitionException.class, () -> {
            decisionService.advanceStage(1L);
        });

        assertTrue(ex.getMessage().contains("Herbert Simon Model requires modeling at least two competing solution alternatives"));
    }

    @Test
    @DisplayName("Simon Guard: Advancing from Design to Choice succeeds when at least 2 alternatives exist")
    void testDesignToChoiceSucceedsWithTwoAlternatives() {
        DecisionInitiative initiative = new DecisionInitiative(
                "Supply Chain Optimization",
                "High fulfillment cycle time detected",
                KpiPerspective.INTERNAL_PROCESS,
                "PROC-01",
                DecisionStage.DESIGN,
                50000.0,
                20.0,
                "Logistics Director",
                "IN_PROGRESS"
        );
        initiative.setId(1L);

        DecisionAlternative alt1 = new DecisionAlternative(
                "Option A", "Desc A", "Pros A", "Cons A", 4, 15.0, 50000.0, false
        );
        DecisionAlternative alt2 = new DecisionAlternative(
                "Option B", "Desc B", "Pros B", "Cons B", 3, 25.0, 75000.0, false
        );
        initiative.addAlternative(alt1);
        initiative.addAlternative(alt2);

        when(initiativeRepository.findById(1L)).thenReturn(Optional.of(initiative));
        when(initiativeRepository.save(any(DecisionInitiative.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DecisionInitiative updated = decisionService.advanceStage(1L);

        assertEquals(DecisionStage.CHOICE, updated.getCurrentStage());
    }

    @Test
    @DisplayName("Simon Guard: Advancing from Choice to Implementation throws exception if no alternative is selected")
    void testChoiceToImplementationBlockedWithoutSelection() {
        DecisionInitiative initiative = new DecisionInitiative(
                "Supply Chain Optimization",
                "High fulfillment cycle time detected",
                KpiPerspective.INTERNAL_PROCESS,
                "PROC-01",
                DecisionStage.CHOICE,
                50000.0,
                20.0,
                "Logistics Director",
                "IN_PROGRESS"
        );
        initiative.setId(1L);

        DecisionAlternative alt1 = new DecisionAlternative(
                "Option A", "Desc A", "Pros A", "Cons A", 4, 15.0, 50000.0, false
        );
        DecisionAlternative alt2 = new DecisionAlternative(
                "Option B", "Desc B", "Pros B", "Cons B", 3, 25.0, 75000.0, false
        );
        initiative.addAlternative(alt1);
        initiative.addAlternative(alt2);
        // Neither alternative selected

        when(initiativeRepository.findById(1L)).thenReturn(Optional.of(initiative));

        InvalidDecisionTransitionException ex = assertThrows(InvalidDecisionTransitionException.class, () -> {
            decisionService.advanceStage(1L);
        });

        assertTrue(ex.getMessage().contains("Stage 3 (Choice) requires selecting and approving one preferred solution alternative"));
    }

    @Test
    @DisplayName("Simon Guard: Advancing from Choice to Implementation succeeds when alternative is selected")
    void testChoiceToImplementationSucceedsWithSelectedAlternative() {
        DecisionInitiative initiative = new DecisionInitiative(
                "Supply Chain Optimization",
                "High fulfillment cycle time detected",
                KpiPerspective.INTERNAL_PROCESS,
                "PROC-01",
                DecisionStage.CHOICE,
                50000.0,
                20.0,
                "Logistics Director",
                "IN_PROGRESS"
        );
        initiative.setId(1L);

        DecisionAlternative alt1 = new DecisionAlternative(
                "Option A", "Desc A", "Pros A", "Cons A", 4, 15.0, 50000.0, false
        );
        alt1.setId(10L);
        DecisionAlternative alt2 = new DecisionAlternative(
                "Option B", "Desc B", "Pros B", "Cons B", 3, 25.0, 75000.0, true
        );
        alt2.setId(20L);
        initiative.addAlternative(alt1);
        initiative.addAlternative(alt2);

        when(initiativeRepository.findById(1L)).thenReturn(Optional.of(initiative));
        when(initiativeRepository.save(any(DecisionInitiative.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DecisionInitiative updated = decisionService.advanceStage(1L);

        assertEquals(DecisionStage.IMPLEMENTATION, updated.getCurrentStage());
    }

    @Test
    @DisplayName("Complete Stage 4 with operational feedback closes feedback loop")
    void testCompleteStageWithFeedback() {
        DecisionInitiative initiative = new DecisionInitiative(
                "Supply Chain Optimization",
                "High fulfillment cycle time detected",
                KpiPerspective.INTERNAL_PROCESS,
                "PROC-01",
                DecisionStage.IMPLEMENTATION,
                50000.0,
                20.0,
                "Logistics Director",
                "IN_PROGRESS"
        );
        initiative.setId(1L);
        initiative.setImplementationFeedback("Cycle time dropped from 6.8 days to 4.2 days post-deployment.");

        when(initiativeRepository.findById(1L)).thenReturn(Optional.of(initiative));
        when(initiativeRepository.save(any(DecisionInitiative.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DecisionInitiative completed = decisionService.advanceStage(1L);

        assertEquals("COMPLETED", completed.getStatus());
        assertTrue(completed.getImplementationFeedback().contains("Cycle time dropped"));
    }
}
