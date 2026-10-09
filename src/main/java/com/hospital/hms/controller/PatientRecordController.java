package com.hospital.hms.controller;

import com.hospital.hms.model.*;
import com.hospital.hms.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/records")
public class PatientRecordController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;
    private final BillingService billingService;

    public PatientRecordController(PatientService patientService,
                                   AppointmentService appointmentService,
                                   PrescriptionService prescriptionService,
                                   BillingService billingService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
        this.prescriptionService = prescriptionService;
        this.billingService = billingService;
    }

    @GetMapping
    public String index(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Patient> patients = patientService.searchPatients(search);
        model.addAttribute("patients", patients);
        model.addAttribute("search", search);
        model.addAttribute("activeNav", "records");
        return "records/index";
    }

    @GetMapping("/{patientId}")
    public String viewPatientHistory(@PathVariable("patientId") Long patientId, Model model, RedirectAttributes redirectAttributes) {
        return patientService.findById(patientId).map(patient -> {
            List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patient);
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatient(patient);
            List<Bill> bills = billingService.getBillsByPatient(patient);

            model.addAttribute("patient", patient);
            model.addAttribute("appointments", appointments);
            model.addAttribute("prescriptions", prescriptions);
            model.addAttribute("bills", bills);
            model.addAttribute("activeNav", "records");
            return "records/patient-history";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Patient not found with ID #" + patientId);
            return "redirect:/records";
        });
    }

    @GetMapping("/by-code")
    public String findByCode(@RequestParam("code") String code, RedirectAttributes redirectAttributes) {
        return patientService.findByPatientCode(code).map(patient ->
                "redirect:/records/" + patient.getId()
        ).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "No patient found with ID: " + code);
            return "redirect:/records";
        });
    }
}
