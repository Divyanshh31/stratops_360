package com.stratops.stratops360.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Controller for the public landing homepage of StratOps 360,
 * featuring the React Bits GhostFibers background and platform capability showcase.
 */
@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Principal principal, Model model) {
        if (principal != null) {
            model.addAttribute("authenticatedUser", principal.getName());
        }
        return "home";
    }
}
