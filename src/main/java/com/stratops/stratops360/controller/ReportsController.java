package com.stratops.stratops360.controller;

import com.stratops.stratops360.model.KpiMetric;
import com.stratops.stratops360.model.KpiPerspective;
import com.stratops.stratops360.model.KpiStatus;
import com.stratops.stratops360.service.KpiService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final KpiService kpiService;

    public ReportsController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @GetMapping
    public String parameterizedReports(
            @RequestParam(value = "department", required = false, defaultValue = "ALL") String department,
            @RequestParam(value = "perspective", required = false, defaultValue = "ALL") String perspective,
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            Model model) {

        List<KpiMetric> filtered = filterKpis(department, perspective, status);

        model.addAttribute("kpis", filtered);
        model.addAttribute("selectedDept", department);
        model.addAttribute("selectedPerspective", perspective);
        model.addAttribute("selectedStatus", status);

        model.addAttribute("departments", List.of("ALL", "Finance", "Treasury", "Sales & Marketing", "Customer Success",
                "Supply Chain", "Marketing", "Logistics", "Quality Assurance", "Plant Operations", "Operations", "Human Resources", "Information Systems"));
        model.addAttribute("perspectives", KpiPerspective.values());
        model.addAttribute("statuses", KpiStatus.values());

        double avgAchievement = filtered.stream().mapToDouble(KpiMetric::getAchievementRatio).average().orElse(0.0);
        long criticalCount = filtered.stream().filter(k -> k.getStatus() == KpiStatus.CRITICAL).count();
        long warningCount = filtered.stream().filter(k -> k.getStatus() == KpiStatus.WARNING).count();
        long onTrackCount = filtered.stream().filter(k -> k.getStatus() == KpiStatus.ON_TRACK).count();

        model.addAttribute("avgAchievement", Math.round(avgAchievement * 10.0) / 10.0);
        model.addAttribute("criticalCount", criticalCount);
        model.addAttribute("warningCount", warningCount);
        model.addAttribute("onTrackCount", onTrackCount);

        return "reports";
    }

    @GetMapping("/export-csv")
    public void exportCsv(
            @RequestParam(value = "department", required = false, defaultValue = "ALL") String department,
            @RequestParam(value = "perspective", required = false, defaultValue = "ALL") String perspective,
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            HttpServletResponse response) throws IOException {

        List<KpiMetric> filtered = filterKpis(department, perspective, status);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"scorecard_reports.csv\"");

        StringBuilder csv = new StringBuilder();
        csv.append("Code,Name,Perspective,Department,Target SLA,Actual Telemetry,Unit,Achievement Ratio %,Status\n");
        for (KpiMetric k : filtered) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%.2f,\"%s\",%.2f,\"%s\"\n",
                    k.getCode(),
                    k.getName().replace("\"", "\"\""),
                    k.getPerspective().name(),
                    k.getDepartment(),
                    k.getTargetValue(),
                    k.getActualValue(),
                    k.getUnit(),
                    k.getAchievementRatio(),
                    k.getStatus().name()));
        }

        response.getWriter().write(csv.toString());
    }

    private List<KpiMetric> filterKpis(String department, String perspective, String status) {
        return kpiService.getActiveKpis().stream()
                .filter(k -> department.equals("ALL") || k.getDepartment().equalsIgnoreCase(department))
                .filter(k -> perspective.equals("ALL") || k.getPerspective().name().equalsIgnoreCase(perspective))
                .filter(k -> status.equals("ALL") || k.getStatus().name().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
}
