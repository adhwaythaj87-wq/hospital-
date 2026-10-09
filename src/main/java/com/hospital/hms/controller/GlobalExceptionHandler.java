package com.hospital.hms.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        model.addAttribute("status", 400);
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("activeNav", "dashboard");
        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException ex, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("message", "The requested page was not found: " + ex.getResourcePath());
        model.addAttribute("activeNav", "dashboard");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("message", ex.getMessage() != null ? ex.getMessage() : "An unexpected clinical system error occurred.");
        model.addAttribute("activeNav", "dashboard");
        return "error";
    }
}
