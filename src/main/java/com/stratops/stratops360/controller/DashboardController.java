package com.stratops.stratops360.controller;

import com.stratops.stratops360.dto.KpiFormDto;
import com.stratops.stratops360.model.KpiMetric;
import com.stratops.stratops360.model.KpiPerspective;
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
public class DashboardController {

    private final KpiService kpiService;
    private final DecisionService decisionService;

    public DashboardController(KpiService kpiService, DecisionService decisionService) {
        this.kpiService = kpiService;
        this.decisionService = decisionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "role", defaultValue = "Senior Management (ESS)") String role,
                            @RequestParam(value = "period", defaultValue = "Current") String period,
                            Model model) {
        model.addAttribute("activeRole", role);
        model.addAttribute("selectedPeriod", period);
        model.addAttribute("summary", kpiService.getOverallScorecardSummary());
        model.addAttribute("allKpis", kpiService.getActiveKpis());

        // Group active KPIs by perspective
        model.addAttribute("financialKpis", kpiService.getKpisByPerspective(KpiPerspective.FINANCIAL));
        model.addAttribute("customerKpis", kpiService.getKpisByPerspective(KpiPerspective.CUSTOMER));
        model.addAttribute("processKpis", kpiService.getKpisByPerspective(KpiPerspective.INTERNAL_PROCESS));
        model.addAttribute("learningKpis", kpiService.getKpisByPerspective(KpiPerspective.LEARNING_GROWTH));

        // Recent initiatives
        model.addAttribute("recentInitiatives", decisionService.getAllInitiatives().stream().limit(5).toList());

        return "dashboard";
    }

    @GetMapping("/perspective/{perspective}")
    public String viewPerspective(@PathVariable("perspective") String perspectiveStr, Model model) {
        String normalized = perspectiveStr != null ? perspectiveStr.toUpperCase() : "";
        if ("INTERNAL_PROCESSES".equals(normalized) || "PROCESS".equals(normalized) || "INTERNAL".equals(normalized)) {
            normalized = "INTERNAL_PROCESS";
        } else if ("LEARNING_AND_GROWTH".equals(normalized) || "LEARNING".equals(normalized) || "GROWTH".equals(normalized)) {
            normalized = "LEARNING_GROWTH";
        }

        KpiPerspective perspective;
        try {
            perspective = KpiPerspective.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return "redirect:/dashboard";
        }

        List<KpiMetric> kpis = kpiService.getKpisByPerspective(perspective);
        model.addAttribute("perspective", perspective);
        model.addAttribute("kpis", kpis);
        model.addAttribute("initiatives", decisionService.getInitiativesByPerspective(perspective));

        return "perspective_drilldown";
    }

    @GetMapping("/kpi/new")
    public String newKpiForm(jakarta.servlet.http.HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot create new strategic scorecard metrics (Requires Manager authority).");
            return "redirect:/dashboard";
        }
        model.addAttribute("kpiForm", new KpiFormDto());
        model.addAttribute("perspectives", KpiPerspective.values());
        return "kpi_new";
    }

    @PostMapping("/kpi/create")
    public String createKpi(@Valid @ModelAttribute("kpiForm") KpiFormDto kpiForm,
                            BindingResult bindingResult,
                            jakarta.servlet.http.HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot create new strategic scorecard metrics (Requires Manager authority).");
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("perspectives", KpiPerspective.values());
            return "kpi_new";
        }

        try {
            kpiService.createKpi(kpiForm);
            redirectAttributes.addFlashAttribute("successMessage", "KPI '" + kpiForm.getCode() + "' successfully created and added to the Scorecard.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("perspectives", KpiPerspective.values());
            model.addAttribute("errorMessage", e.getMessage());
            return "kpi_new";
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/kpi/edit/{id}")
    public String editKpiForm(@PathVariable("id") Long id,
                              jakarta.servlet.http.HttpSession session,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot modify strategic KPI targets (Requires Manager authority).");
            return "redirect:/dashboard";
        }
        KpiMetric kpi = kpiService.getKpiById(id);
        KpiFormDto dto = new KpiFormDto();
        dto.setId(kpi.getId());
        dto.setCode(kpi.getCode());
        dto.setName(kpi.getName());
        dto.setPerspective(kpi.getPerspective());
        dto.setTargetValue(kpi.getTargetValue());
        dto.setActualValue(kpi.getActualValue());
        dto.setUnit(kpi.getUnit());
        dto.setDepartment(kpi.getDepartment());
        dto.setDescription(kpi.getDescription());
        dto.setHigherIsBetter(kpi.isHigherIsBetter());

        model.addAttribute("kpiForm", dto);
        model.addAttribute("kpiCode", kpi.getCode());
        model.addAttribute("auditLogs", kpiService.getAuditLogs(kpi.getCode()));
        return "kpi_edit";
    }

    @PostMapping("/kpi/save")
    public String saveKpi(@Valid @ModelAttribute("kpiForm") KpiFormDto kpiForm,
                          BindingResult bindingResult,
                          jakarta.servlet.http.HttpSession session,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot modify strategic KPI targets (Requires Manager authority).");
            return "redirect:/dashboard";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("kpiCode", kpiForm.getCode());
            model.addAttribute("auditLogs", kpiService.getAuditLogs(kpiForm.getCode()));
            return "kpi_edit";
        }

        kpiService.updateKpi(kpiForm, "Executive Officer");
        redirectAttributes.addFlashAttribute("successMessage", "KPI '" + kpiForm.getCode() + "' updated. Scorecard health recalculated.");
        return "redirect:/dashboard";
    }

    @PostMapping("/kpi/deactivate/{id}")
    public String deactivateKpi(@PathVariable("id") Long id, jakarta.servlet.http.HttpSession session, RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot archive scorecard metrics.");
            return "redirect:/dashboard";
        }
        kpiService.deactivateKpi(id);
        redirectAttributes.addFlashAttribute("successMessage", "KPI successfully archived/deactivated.");
        return "redirect:/dashboard";
    }

    @GetMapping("/search")
    public String globalSearch(@RequestParam(value = "q", required = false) String query, Model model) {
        model.addAttribute("query", query);
        if (query != null && !query.isBlank()) {
            model.addAttribute("matchingKpis", kpiService.getAllKpis().stream()
                    .filter(k -> k.getName().toLowerCase().contains(query.toLowerCase()) ||
                            k.getCode().toLowerCase().contains(query.toLowerCase()) ||
                            k.getDepartment().toLowerCase().contains(query.toLowerCase()))
                    .toList());
            model.addAttribute("matchingInitiatives", decisionService.searchInitiatives(query));
        }
        return "search_results";
    }
}
