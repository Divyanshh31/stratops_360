package com.stratops.stratops360.controller;

import com.stratops.stratops360.dto.CreditEvaluationFormDto;
import com.stratops.stratops360.dto.SimulationFormDto;
import com.stratops.stratops360.model.*;
import com.stratops.stratops360.service.DecisionService;
import com.stratops.stratops360.service.ExpertSystemEngine;
import com.stratops.stratops360.service.KpiService;
import com.stratops.stratops360.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "StratOps 360 MIS APIs", description = "REST endpoints for enterprise scorecard telemetry, decision pipeline, DSS simulations, and rule-based expert evaluation.")
public class RestApiController {

    private final KpiService kpiService;
    private final DecisionService decisionService;
    private final SimulationService simulationService;
    private final ExpertSystemEngine expertEngine;

    public RestApiController(KpiService kpiService,
                             DecisionService decisionService,
                             SimulationService simulationService,
                             ExpertSystemEngine expertEngine) {
        this.kpiService = kpiService;
        this.decisionService = decisionService;
        this.simulationService = simulationService;
        this.expertEngine = expertEngine;
    }

    @GetMapping({"/summary", "/scorecard/summary"})
    @Operation(summary = "Get overall Balanced Scorecard health summary and perspective scores")
    public ResponseEntity<Map<String, Object>> getScorecardSummary() {
        return ResponseEntity.ok(kpiService.getOverallScorecardSummary());
    }

    @GetMapping("/kpis")
    @Operation(summary = "Get all active KPI metrics across the 4 Balanced Scorecard perspectives")
    public ResponseEntity<List<KpiMetric>> getAllKpis() {
        return ResponseEntity.ok(kpiService.getActiveKpis());
    }

    @GetMapping("/kpis/{code}")
    @Operation(summary = "Get a specific KPI metric by its code (e.g. FIN-01, PROC-01)")
    public ResponseEntity<KpiMetric> getKpiByCode(@PathVariable("code") String code) {
        return kpiService.getKpiByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/decisions")
    @Operation(summary = "Get all active initiatives in Herbert Simon's decision pipeline")
    public ResponseEntity<List<DecisionInitiative>> getDecisions() {
        return ResponseEntity.ok(decisionService.getAllInitiatives());
    }

    @PostMapping("/simulation/simulate")
    @Operation(summary = "Run What-If scenario simulation against baseline scorecard assumptions")
    public ResponseEntity<ScenarioSimulationResult> simulateScenario(@Valid @RequestBody SimulationFormDto form) {
        return ResponseEntity.ok(simulationService.simulate(form));
    }

    @PostMapping("/expert-system/evaluate")
    @Operation(summary = "Execute forward-chaining credit line expert evaluation")
    public ResponseEntity<CreditRuleEvaluation> evaluateCredit(@Valid @RequestBody CreditEvaluationFormDto form) {
        return ResponseEntity.ok(expertEngine.evaluateAndSave(form));
    }
}
