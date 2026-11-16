package com.stratops.stratops360.controller;

import com.stratops.stratops360.dto.CreditEvaluationFormDto;
import com.stratops.stratops360.dto.RuleThresholdConfigFormDto;
import com.stratops.stratops360.model.CreditRuleEvaluation;
import com.stratops.stratops360.model.RuleThresholdConfig;
import com.stratops.stratops360.service.ExpertSystemEngine;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;

@Controller
@RequestMapping("/expert-system")
public class ExpertSystemController {

    private final ExpertSystemEngine expertEngine;

    public ExpertSystemController(ExpertSystemEngine expertEngine) {
        this.expertEngine = expertEngine;
    }

    @GetMapping
    public String expertSystemPage(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "8") int size,
                                   Model model) {
        Page<CreditRuleEvaluation> pagedEvaluations = expertEngine.getEvaluationsPaged(PageRequest.of(page, size));

        model.addAttribute("evaluationsPage", pagedEvaluations);
        model.addAttribute("thresholds", expertEngine.getThresholdConfig());

        if (!model.containsAttribute("evaluationForm")) {
            CreditEvaluationFormDto dto = new CreditEvaluationFormDto(
                    "David Vance",
                    new BigDecimal("72000.00"),
                    new BigDecimal("350.00"),
                    new BigDecimal("1050.00"),
                    5,
                    new BigDecimal("120.00")
            );
            model.addAttribute("evaluationForm", dto);
        }

        return "expert_system";
    }

    @PostMapping("/evaluate")
    public String evaluateCredit(@Valid @ModelAttribute("evaluationForm") CreditEvaluationFormDto form,
                                 BindingResult bindingResult,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("evaluationsPage", expertEngine.getEvaluationsPaged(PageRequest.of(page, 8)));
            model.addAttribute("thresholds", expertEngine.getThresholdConfig());
            model.addAttribute("errorMessage", "Validation failed. Please correct input fields.");
            return "expert_system";
        }

        CreditRuleEvaluation result = expertEngine.evaluateAndSave(form);
        model.addAttribute("latestEvaluation", result);
        model.addAttribute("evaluationsPage", expertEngine.getEvaluationsPaged(PageRequest.of(0, 8)));
        model.addAttribute("thresholds", expertEngine.getThresholdConfig());
        model.addAttribute("evaluationForm", form);

        if (result.isApproved()) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Application Approved: Forward-chaining reached " + result.getOutcomeState());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Application Rejected: " + result.getOutcomeState());
        }

        return "expert_system";
    }

    @GetMapping("/thresholds")
    public String editThresholdsForm(jakarta.servlet.http.HttpSession session,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot tune inference engine parameters (Requires Manager authority).");
            return "redirect:/expert-system";
        }
        RuleThresholdConfig current = expertEngine.getThresholdConfig();
        RuleThresholdConfigFormDto dto = new RuleThresholdConfigFormDto();
        dto.setMinIncome(current.getMinIncome());
        dto.setMaxCarRatio(current.getMaxCarRatio());
        dto.setMaxMortgageRatio(current.getMaxMortgageRatio());
        dto.setMinTenureYears(current.getMinTenureYears());
        dto.setMaxOtherDebtRatio(current.getMaxOtherDebtRatio());
        dto.setTier1Limit(current.getTier1Limit());
        dto.setTier2Limit(current.getTier2Limit());

        model.addAttribute("thresholdForm", dto);
        model.addAttribute("currentConfig", current);
        return "expert_system_thresholds";
    }

    @PostMapping("/thresholds/save")
    public String saveThresholds(@Valid @ModelAttribute("thresholdForm") RuleThresholdConfigFormDto form,
                                 BindingResult bindingResult,
                                 jakarta.servlet.http.HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot modify credit risk policy thresholds (Requires Manager authority).");
            return "redirect:/expert-system";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentConfig", expertEngine.getThresholdConfig());
            return "expert_system_thresholds";
        }

        expertEngine.updateThresholdConfig(form, "Risk Officer");
        redirectAttributes.addFlashAttribute("successMessage",
                "Knowledge Base rule thresholds updated successfully. All future forward-chaining evaluations will use the new parameters.");
        return "redirect:/expert-system";
    }

    @GetMapping("/export-csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"credit_rule_evaluations.csv\"");
        String csvData = expertEngine.exportEvaluationsCsv();
        response.getWriter().write(csvData);
    }
}
