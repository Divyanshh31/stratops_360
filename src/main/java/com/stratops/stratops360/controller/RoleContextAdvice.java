package com.stratops.stratops360.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Provides MIS Organizational Hierarchy Role Context to all Thymeleaf templates.
 * Based on Robert Anthony's Triangle & Kenneth Laudon MIS Management Levels:
 * - Executive: Strategic Level (ESS - Executive Support Systems)
 * - Manager: Tactical Level (MIS / DSS - Decision Support Systems)
 * - Operations: Operational Level (TPS - Transaction Processing Systems)
 */
@ControllerAdvice
public class RoleContextAdvice {

    public static final String SESSION_ROLE_KEY = "stratops_role";

    @ModelAttribute
    public void populateRoleContext(HttpServletRequest request, HttpSession session, Model model) {
        String role = null;

        // 1. Check Spring Security Authenticated Principal first
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String principalName = auth.getName();
            if ("executive".equalsIgnoreCase(principalName)) {
                role = "Executive";
            } else if ("operations".equalsIgnoreCase(principalName)) {
                role = "Operations";
            } else if ("manager".equalsIgnoreCase(principalName)) {
                role = "Manager";
            }
        }

        // 2. Fallback to Session
        if (role == null || role.isBlank()) {
            role = (String) session.getAttribute(SESSION_ROLE_KEY);
        }

        // 3. Fallback to Persistent Cookie
        if (role == null || role.isBlank()) {
            if (request != null && request.getCookies() != null) {
                for (Cookie c : request.getCookies()) {
                    if (SESSION_ROLE_KEY.equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                        role = c.getValue();
                        break;
                    }
                }
            }
        }

        // 4. Default standard middle management
        if (role == null || role.isBlank()) {
            role = "Manager";
        }
        session.setAttribute(SESSION_ROLE_KEY, role);

        String normalized = role.trim();
        boolean isOps = "Operations".equalsIgnoreCase(normalized);
        boolean isMgr = "Manager".equalsIgnoreCase(normalized);
        boolean isExec = "Executive".equalsIgnoreCase(normalized);

        model.addAttribute("currentRole", normalized);
        model.addAttribute("isOperations", isOps);
        model.addAttribute("isManager", isMgr);
        model.addAttribute("isExecutive", isExec);

        if (isOps) {
            model.addAttribute("currentUsername", "operations");
            model.addAttribute("currentDisplayName", "David Chen (Operations Officer)");
            model.addAttribute("misTierTitle", "Operational Level (TPS)");
            model.addAttribute("misTierDescription",
                    "Routine transaction processing and operational telemetry monitoring. Strategic target modification and decision phase transitions are restricted.");
        } else if (isExec) {
            model.addAttribute("currentUsername", "executive");
            model.addAttribute("currentDisplayName", "Divyansh Thakur (Executive)");
            model.addAttribute("misTierTitle", "Strategic Level (ESS)");
            model.addAttribute("misTierDescription",
                    "Executive summary scorecard, macro-level planning, and What-If sensitivity forecasting enabled.");
        } else {
            model.addAttribute("currentUsername", "manager");
            model.addAttribute("currentDisplayName", "Divyansh Thakur (Manager)");
            model.addAttribute("misTierTitle", "Tactical Level (MIS / DSS)");
            model.addAttribute("misTierDescription",
                    "Middle management decision pipeline control, KPI target administration, and rule threshold customization enabled.");
        }

        // Credentials reference for demo transparency
        model.addAttribute("demoExecUser", RoleSwitchController.EXEC_USER);
        model.addAttribute("demoExecPass", RoleSwitchController.EXEC_PASS);
        model.addAttribute("demoMgrUser", RoleSwitchController.MGR_USER);
        model.addAttribute("demoMgrPass", RoleSwitchController.MGR_PASS);
        model.addAttribute("demoOpsUser", RoleSwitchController.OPS_USER);
        model.addAttribute("demoOpsPass", RoleSwitchController.OPS_PASS);
    }
}