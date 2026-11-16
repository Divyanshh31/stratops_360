package com.stratops.stratops360.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleSwitchController {

    // Defined System Personnel Credentials
    public static final String EXEC_USER = "executive";
    public static final String EXEC_PASS = "executive123";

    public static final String MGR_USER = "manager";
    public static final String MGR_PASS = "manager123";

    public static final String OPS_USER = "operations";
    public static final String OPS_PASS = "operations123";

    @PostMapping("/switch")
    public String switchRolePost(@RequestParam("role") String role,
                                 @RequestParam(value = "username", required = false) String username,
                                 @RequestParam(value = "password", required = false) String password,
                                 @RequestParam(value = "redirectUrl", required = false) String redirectUrl,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        return processSwitch(role, username, password, redirectUrl, request, response, session, redirectAttributes);
    }

    @GetMapping("/switch")
    public String switchRoleGet(@RequestParam("role") String role,
                                @RequestParam(value = "username", required = false) String username,
                                @RequestParam(value = "password", required = false) String password,
                                @RequestParam(value = "redirectUrl", required = false) String redirectUrl,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        return processSwitch(role, username, password, redirectUrl, request, response, session, redirectAttributes);
    }

    @ResponseBody
    @PostMapping("/api/switch")
    public ResponseEntity<Map<String, Object>> switchRoleApi(@RequestParam("role") String role,
                                                             @RequestParam(value = "username", required = false) String username,
                                                             @RequestParam(value = "password", required = false) String password,
                                                             HttpServletResponse response,
                                                             HttpSession session) {
        String normalized = normalize(role);
        if (username != null && password != null && !password.isBlank()) {
            boolean valid = validateCredentials(normalized, username, password);
            if (!valid) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Authentication Failed: Incorrect Login ID or Password for " + normalized + "."
                ));
            }
        }

        session.setAttribute(RoleContextAdvice.SESSION_ROLE_KEY, normalized);
        setRoleCookie(response, normalized);
        updateSecurityContext(normalized);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "role", normalized,
                "isOperations", "Operations".equalsIgnoreCase(normalized),
                "isManager", "Manager".equalsIgnoreCase(normalized),
                "isExecutive", "Executive".equalsIgnoreCase(normalized)
        ));
    }

    private String processSwitch(String role, String username, String password, String redirectUrl,
                                 HttpServletRequest request, HttpServletResponse response,
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        String normalized = normalize(role);

        // If credentials were provided, enforce rigorous authentication check
        if (username != null && password != null && !password.isBlank()) {
            boolean valid = validateCredentials(normalized, username, password);
            if (!valid) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Security Clearance Denied: Invalid Login ID or Password for " + normalized + " role.");
                return getSafeRedirect(redirectUrl, request);
            }
        }

        session.setAttribute(RoleContextAdvice.SESSION_ROLE_KEY, normalized);
        setRoleCookie(response, normalized);
        updateSecurityContext(normalized);

        if ("Operations".equalsIgnoreCase(normalized)) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Switched to Operational Level (TPS): Limited access applied. Metric editing and decision phase advancement restricted.");
        } else if ("Executive".equalsIgnoreCase(normalized)) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Authenticated as Senior Executive: Strategic Level (ESS) unlocked. Macro scorecard and What-If forecasting active.");
        } else {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Authenticated as Department Manager: Tactical Level (MIS / DSS) active. Full administrative control enabled.");
        }

        return getSafeRedirect(redirectUrl, request);
    }

    private boolean validateCredentials(String role, String username, String password) {
        if ("Executive".equalsIgnoreCase(role)) {
            return EXEC_USER.equalsIgnoreCase(username.trim()) && EXEC_PASS.equals(password);
        } else if ("Operations".equalsIgnoreCase(role)) {
            return OPS_USER.equalsIgnoreCase(username.trim()) && OPS_PASS.equals(password);
        } else {
            return MGR_USER.equalsIgnoreCase(username.trim()) && MGR_PASS.equals(password);
        }
    }

    private void updateSecurityContext(String role) {
        String username = "manager";
        String authority = "ROLE_MANAGER";
        if ("Executive".equalsIgnoreCase(role)) {
            username = "executive";
            authority = "ROLE_EXECUTIVE";
        } else if ("Operations".equalsIgnoreCase(role)) {
            username = "operations";
            authority = "ROLE_OPERATIONS";
        }

        UserDetails userDetails = User.withUsername(username)
                .password("")
                .authorities(List.of(new SimpleGrantedAuthority(authority), new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String getSafeRedirect(String redirectUrl, HttpServletRequest request) {
        if (redirectUrl != null && !redirectUrl.isBlank() && redirectUrl.startsWith("/") && !redirectUrl.contains("error")) {
            return "redirect:" + redirectUrl;
        }

        String referer = request.getHeader("Referer");
        if (referer != null && referer.contains("localhost:8080") && !referer.contains("/role/switch") && !referer.contains("/login")) {
            return "redirect:" + referer;
        }

        return "redirect:/dashboard";
    }

    private void setRoleCookie(HttpServletResponse response, String role) {
        Cookie cookie = new Cookie("stratops_role", role);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
    }

    private String normalize(String raw) {
        if (raw == null) return "Manager";
        String lower = raw.trim().toLowerCase();
        if (lower.contains("oper")) return "Operations";
        if (lower.contains("exec")) return "Executive";
        return "Manager";
    }
}