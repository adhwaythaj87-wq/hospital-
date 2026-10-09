package com.hospital.hms.controller;

import com.hospital.hms.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        Map<String, Object> stats = dashboardService.getDashboardData();
        model.addAllAttributes(stats);
        model.addAttribute("activeNav", "dashboard");
        return "dashboard/index";
    }
}
