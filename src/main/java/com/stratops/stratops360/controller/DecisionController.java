package com.stratops.stratops360.controller;

import com.stratops.stratops360.dto.AlternativeFormDto;
import com.stratops.stratops360.dto.DecisionInitiativeFormDto;
import com.stratops.stratops360.exception.InvalidDecisionTransitionException;
import com.stratops.stratops360.model.*;
import com.stratops.stratops360.service.DecisionService;
import com.stratops.stratops360.service.KpiService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/decision")
public class DecisionController {

    private final DecisionService decisionService;
    private final KpiService kpiService;

    public DecisionController(DecisionService decisionService, KpiService kpiService) {
        this.decisionService = decisionService;
        this.kpiService = kpiService;
    }

    @GetMapping("/pipeline")
    public String decisionPipeline(@RequestParam(value = "search", required = false) String search, Model model) {
        List<DecisionInitiative> all = (search != null && !search.isBlank())
                ? decisionService.searchInitiatives(search)
                : decisionService.getAllInitiatives();

        model.addAttribute("searchQuery", search);
        model.addAttribute("stage1", all.stream().filter(i -> i.getCurrentStage() == DecisionStage.INTELLIGENCE).toList());
        model.addAttribute("stage2", all.stream().filter(i -> i.getCurrentStage() == DecisionStage.DESIGN).toList());
        model.addAttribute("stage3", all.stream().filter(i -> i.getCurrentStage() == DecisionStage.CHOICE).toList());
        model.addAttribute("stage4", all.stream().filter(i -> i.getCurrentStage() == DecisionStage.IMPLEMENTATION).toList());
        return "decision_pipeline";
    }

    @GetMapping("/new")
    public String newInitiativeForm(@RequestParam(value = "kpiCode", required = false) String kpiCode,
                                    jakarta.servlet.http.HttpSession session,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot create new decision initiatives (Requires Manager authority).");
            return "redirect:/decision/pipeline";
        }
        DecisionInitiativeFormDto dto = new DecisionInitiativeFormDto();
        if (kpiCode != null) {
            kpiService.getKpiByCode(kpiCode).ifPresent(k -> {
                dto.setRelatedKpiCode(k.getCode());
                dto.setPerspective(k.getPerspective());
                dto.setTitle("Remediation Initiative for " + k.getName());
                dto.setProblemStatement(String.format("Discrepancy detected in %s (%s): Current telemetry of %s %s deviates significantly from Target SLA of %s %s.",
                        k.getName(), k.getCode(), k.getActualValue(), k.getUnit(), k.getTargetValue(), k.getUnit()));
            });
        }
        model.addAttribute("initiativeForm", dto);
        model.addAttribute("perspectives", KpiPerspective.values());
        model.addAttribute("kpis", kpiService.getActiveKpis());
        return "decision_new";
    }

    @PostMapping("/create")
    public String createInitiative(@Valid @ModelAttribute("initiativeForm") DecisionInitiativeFormDto form,
                                   BindingResult bindingResult,
                                   jakarta.servlet.http.HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot formulate new Simon decision initiatives (Requires Manager authority).");
            return "redirect:/decision/pipeline";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("perspectives", KpiPerspective.values());
            model.addAttribute("kpis", kpiService.getActiveKpis());
            return "decision_new";
        }

        DecisionInitiative saved = decisionService.createInitiative(form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Initiative registered under Herbert Simon Stage 1 (Intelligence): Problem Discovered.");
        return "redirect:/decision/view/" + saved.getId();
    }

    @GetMapping("/view/{id}")
    public String viewInitiative(@PathVariable("id") Long id, Model model) {
        DecisionInitiative initiative = decisionService.getInitiativeById(id);
        AlternativeFormDto altDto = new AlternativeFormDto();
        altDto.setInitiativeId(initiative.getId());

        model.addAttribute("initiative", initiative);
        model.addAttribute("alternativeForm", altDto);
        return "decision_view";
    }

    @PostMapping("/add-alternative")
    public String addAlternative(@Valid @ModelAttribute("alternativeForm") AlternativeFormDto altForm,
                                 BindingResult bindingResult,
                                 jakarta.servlet.http.HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Solution alternative configuration requires Tactical Manager authority.");
            return "redirect:/decision/view/" + altForm.getInitiativeId();
        }

        if (bindingResult.hasErrors()) {
            DecisionInitiative initiative = decisionService.getInitiativeById(altForm.getInitiativeId());
            model.addAttribute("initiative", initiative);
            model.addAttribute("errorMessage", "Please correct the input errors below before adding the alternative.");
            return "decision_view";
        }

        decisionService.addAlternative(altForm);
        redirectAttributes.addFlashAttribute("successMessage",
                "Solution alternative '" + altForm.getTitle() + "' successfully added to Stage 2 (Design).");
        return "redirect:/decision/view/" + altForm.getInitiativeId();
    }

    @PostMapping("/select-alternative")
    public String selectAlternative(@RequestParam("initiativeId") Long initiativeId,
                                    @RequestParam("alternativeId") Long alternativeId,
                                    jakarta.servlet.http.HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Alternative selection requires Tactical Manager or Executive authority.");
            return "redirect:/decision/view/" + initiativeId;
        }

        decisionService.selectAlternative(initiativeId, alternativeId);
        redirectAttributes.addFlashAttribute("successMessage",
                "Alternative approved as preferred choice (Stage 3: Choice). Ready for implementation.");
        return "redirect:/decision/view/" + initiativeId;
    }

    @PostMapping("/advance/{id}")
    public String advanceStage(@PathVariable("id") Long id,
                               jakarta.servlet.http.HttpSession session,
                               RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Advancing Herbert Simon decision phases requires Tactical Manager authority.");
            return "redirect:/decision/view/" + id;
        }

        try {
            DecisionInitiative updated = decisionService.advanceStage(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Initiative advanced to Herbert Simon: " + updated.getCurrentStage().getStageName());
        } catch (InvalidDecisionTransitionException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/decision/view/" + id;
    }

    @PostMapping("/feedback/{id}")
    public String updateFeedback(@PathVariable("id") Long id,
                                 @RequestParam("feedback") String feedback,
                                 jakarta.servlet.http.HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot sign off on Stage 4 feedback telemetry.");
            return "redirect:/decision/view/" + id;
        }
        if (feedback == null || feedback.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Feedback telemetry cannot be empty.");
            return "redirect:/decision/view/" + id;
        }

        decisionService.updateFeedback(id, feedback);
        redirectAttributes.addFlashAttribute("successMessage",
                "Stage 4 Implementation feedback loop successfully updated.");
        return "redirect:/decision/view/" + id;
    }
}
