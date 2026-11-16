package com.stratops.stratops360.controller;

import com.stratops.stratops360.dto.SimulationFormDto;
import com.stratops.stratops360.model.SavedScenario;
import com.stratops.stratops360.model.ScenarioSimulationResult;
import com.stratops.stratops360.service.SimulationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @GetMapping
    public String simulationPage(@ModelAttribute("simulationForm") SimulationFormDto form, Model model) {
        ScenarioSimulationResult result = simulationService.simulate(form);
        model.addAttribute("result", result);
        model.addAttribute("simulationForm", form);
        model.addAttribute("savedScenarios", simulationService.getSavedScenarios());
        return "simulation";
    }

    @PostMapping("/run")
    public String runSimulation(@Valid @ModelAttribute("simulationForm") SimulationFormDto form,
                                BindingResult bindingResult,
                                jakarta.servlet.http.HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        String role = (String) session.getAttribute(RoleContextAdvice.SESSION_ROLE_KEY);
        if ("Operations".equalsIgnoreCase(role)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Access Denied: Operational personnel cannot execute or save strategic sensitivity simulations. Requires Executive or Manager authority.");
            return "redirect:/simulation";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("result", simulationService.simulate(new SimulationFormDto()));
            model.addAttribute("savedScenarios", simulationService.getSavedScenarios());
            model.addAttribute("errorMessage", "Invalid slider parameters. Please check bounded values.");
            return "simulation";
        }

        ScenarioSimulationResult result = simulationService.simulate(form);
        model.addAttribute("result", result);
        model.addAttribute("simulationForm", form);
        model.addAttribute("savedScenarios", simulationService.getSavedScenarios());

        if (form.isSaveScenario()) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Scenario '" + form.getScenarioName() + "' has been saved to your organizational scenarios repository.");
            return "redirect:/simulation";
        }

        return "simulation";
    }

    @GetMapping("/compare")
    public String compareScenarios(@RequestParam(value = "ids", required = false) List<Long> ids, Model model) {
        List<SavedScenario> allSaved = simulationService.getSavedScenarios();
        model.addAttribute("allSaved", allSaved);

        if (ids != null && !ids.isEmpty()) {
            List<SavedScenario> selected = allSaved.stream()
                    .filter(s -> ids.contains(s.getId()))
                    .limit(3)
                    .toList();
            model.addAttribute("selectedScenarios", selected);
        }
        return "simulation_compare";
    }

    @PostMapping("/delete/{id}")
    public String deleteScenario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        simulationService.deleteSavedScenario(id);
        redirectAttributes.addFlashAttribute("successMessage", "Saved scenario removed.");
        return "redirect:/simulation";
    }
}
