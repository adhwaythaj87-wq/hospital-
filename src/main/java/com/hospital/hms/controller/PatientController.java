package com.hospital.hms.controller;

import com.hospital.hms.model.Patient;
import com.hospital.hms.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public String listPatients(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Patient> patients = patientService.searchPatients(search);
        model.addAttribute("patients", patients);
        model.addAttribute("search", search);
        model.addAttribute("totalCount", patientService.getTotalPatientsCount());
        model.addAttribute("activeNav", "patients");
        return "patients/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Patient patient = new Patient();
        patient.setPatientCode(patientService.generateNextPatientCode());
        patient.setGender("Male");
        model.addAttribute("patient", patient);
        model.addAttribute("isEdit", false);
        model.addAttribute("activeNav", "patients");
        return "patients/form";
    }

    @PostMapping("/save")
    public String savePatient(@Valid @ModelAttribute("patient") Patient patient,
                              BindingResult bindingResult,
                              @RequestParam(value = "isEdit", defaultValue = "false") boolean isEdit,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        // Check for duplicate patient code on creation or changed code
        if (!isEdit && patientService.existsByPatientCode(patient.getPatientCode())) {
            bindingResult.rejectValue("patientCode", "duplicate", "A patient with ID '" + patient.getPatientCode() + "' already exists.");
        } else if (isEdit && patient.getId() != null) {
            patientService.findByPatientCode(patient.getPatientCode()).ifPresent(existing -> {
                if (!existing.getId().equals(patient.getId())) {
                    bindingResult.rejectValue("patientCode", "duplicate", "A patient with ID '" + patient.getPatientCode() + "' already exists.");
                }
            });
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("activeNav", "patients");
            return "patients/form";
        }

        patientService.savePatient(patient);
        String actionMsg = isEdit ? "updated" : "registered";
        redirectAttributes.addFlashAttribute("successMessage", "Patient " + patient.getFullName() + " (" + patient.getPatientCode() + ") " + actionMsg + " successfully.");
        return "redirect:/patients";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return patientService.findById(id).map(patient -> {
            model.addAttribute("patient", patient);
            model.addAttribute("isEdit", true);
            model.addAttribute("activeNav", "patients");
            return "patients/form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Patient not found with ID #" + id);
            return "redirect:/patients";
        });
    }

    @PostMapping("/{id}/delete")
    public String deletePatient(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            patientService.findById(id).ifPresent(patient -> {
                patientService.deletePatient(id);
                redirectAttributes.addFlashAttribute("successMessage", "Patient " + patient.getFullName() + " (" + patient.getPatientCode() + ") was removed.");
            });
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete patient because they have linked medical records (appointments/prescriptions/bills).");
        }
        return "redirect:/patients";
    }
}
