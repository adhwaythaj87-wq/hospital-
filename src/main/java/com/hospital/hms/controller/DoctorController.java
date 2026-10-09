package com.hospital.hms.controller;

import com.hospital.hms.model.Doctor;
import com.hospital.hms.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public String listDoctors(@RequestParam(value = "search", required = false) String search,
                              @RequestParam(value = "specialization", required = false) String specialization,
                              Model model) {
        List<Doctor> doctors;
        if (search != null && !search.trim().isEmpty()) {
            doctors = doctorService.searchDoctors(search);
        } else if (specialization != null && !specialization.trim().isEmpty()) {
            doctors = doctorService.getDoctorsBySpecialization(specialization);
        } else {
            doctors = doctorService.getAllDoctors();
        }

        model.addAttribute("doctors", doctors);
        model.addAttribute("search", search);
        model.addAttribute("currentSpecialization", specialization);
        model.addAttribute("specializations", doctorService.getAllSpecializations());
        model.addAttribute("totalCount", doctorService.getTotalDoctorsCount());
        model.addAttribute("activeNav", "doctors");
        return "doctors/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Doctor doctor = new Doctor();
        doctor.setDoctorCode(doctorService.generateNextDoctorCode());
        doctor.setConsultationFee(new BigDecimal("500.00"));
        doctor.setStatus("ACTIVE");
        model.addAttribute("doctor", doctor);
        model.addAttribute("isEdit", false);
        model.addAttribute("activeNav", "doctors");
        return "doctors/form";
    }

    @PostMapping("/save")
    public String saveDoctor(@Valid @ModelAttribute("doctor") Doctor doctor,
                             BindingResult bindingResult,
                             @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        // Validate unique doctor code
        if (!isEdit && doctorService.existsByDoctorCode(doctor.getDoctorCode())) {
            bindingResult.rejectValue("doctorCode", "duplicate", "A doctor with ID '" + doctor.getDoctorCode() + "' already exists.");
        } else if (isEdit && doctor.getId() != null) {
            doctorService.findByDoctorCode(doctor.getDoctorCode()).ifPresent(existing -> {
                if (!existing.getId().equals(doctor.getId())) {
                    bindingResult.rejectValue("doctorCode", "duplicate", "A doctor with ID '" + doctor.getDoctorCode() + "' already exists.");
                }
            });
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("activeNav", "doctors");
            return "doctors/form";
        }

        doctorService.saveDoctor(doctor);
        String actionMsg = isEdit ? "updated" : "added";
        redirectAttributes.addFlashAttribute("successMessage", doctor.getFullName() + " (" + doctor.getDoctorCode() + ") " + actionMsg + " successfully.");
        return "redirect:/doctors";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return doctorService.findById(id).map(doctor -> {
            model.addAttribute("doctor", doctor);
            model.addAttribute("isEdit", true);
            model.addAttribute("activeNav", "doctors");
            return "doctors/form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Doctor not found with ID #" + id);
            return "redirect:/doctors";
        });
    }

    @PostMapping("/{id}/delete")
    public String deleteDoctor(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            doctorService.findById(id).ifPresent(doctor -> {
                doctorService.deleteDoctor(id);
                redirectAttributes.addFlashAttribute("successMessage", "Doctor " + doctor.getFullName() + " removed successfully.");
            });
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete doctor because they have associated appointment or medical records.");
        }
        return "redirect:/doctors";
    }
}
