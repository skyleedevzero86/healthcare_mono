package com.sleekydz86.web.controller;

import com.sleekydz86.web.client.HealthcareClient;
import com.sleekydz86.web.client.UserClient;
import com.sleekydz86.web.dto.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private final HealthcareClient healthcareClient;
    private final UserClient userClient;

    public WebController(HealthcareClient healthcareClient, UserClient userClient) {
        this.healthcareClient = healthcareClient;
        this.userClient = userClient;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/patients")
    public String patients(Model model) {
        return "patients";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            User user = userClient.getCurrentUser("Bearer " + token);
            model.addAttribute("user", user);
        }
        return "dashboard";
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
