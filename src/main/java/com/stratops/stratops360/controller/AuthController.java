package com.stratops.stratops360.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

/**
 * Controller for handling authentication view and demo personnel login.
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "role", required = false) String prefillRole,
                            Principal principal,
                            HttpSession session,
                            Model model) {
        if (principal != null) {
            return "redirect:/dashboard";
        }

        if (error != null) {
            model.addAttribute("loginError", "Authentication Failed: Invalid Login ID or Password. Please try again or use a demo account.");
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been successfully signed out. Security session cleared.");
        }

        model.addAttribute("prefillRole", prefillRole != null ? prefillRole : "Manager");
        return "login";
    }
}