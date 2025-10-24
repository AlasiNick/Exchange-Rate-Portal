package com.seb.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/swagger-ui.html";
    }

    @GetMapping("/db")
    public String db() {
        return "redirect:/h2-console";
    }
}
