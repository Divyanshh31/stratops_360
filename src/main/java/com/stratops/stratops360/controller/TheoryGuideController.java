package com.stratops.stratops360.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TheoryGuideController {

    @GetMapping("/theory-guide")
    public String theoryGuide() {
        return "theory_guide";
    }
}
