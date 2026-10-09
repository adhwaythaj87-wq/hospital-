package com.hospital.hms.controller;

import com.hospital.hms.dto.PrescriptionForm;
import com.hospital.hms.model.Doctor;
import com.hospital.hms.model.Patient;
import com.hospital.hms.model.Prescription;
import com.hospital.hms.model.PrescriptionItem;
import com.hospital.hms.service.AppointmentService;
import com.hospital.hms.service.DoctorService;
import com.hospital.hms.service.PatientService;
import com.hospital.hms.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    @Value("${hospital.name:LifeCare Multispeciality Hospital}")
    private String hospitalName;

    @Value("${hospital.tagline:Compassionate Care, Clinical Excellence}")
    private String hospitalTagline;

    @Value("${hospital.address:100 Healthcare Avenue, Medical District, Kochi}")
    private String hospitalAddress;

    @Value("${hospital.phone:+91 (0484) 285-4000}")
    private String hospitalPhone;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  PatientService patientService,
                                  DoctorService doctorService,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public String listPrescriptions(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Prescription> prescriptions = prescriptionService.searchPrescriptions(search);
        model.addAttribute("prescriptions", prescriptions);
        model.addAttribute("search", search);
        model.addAttribute("totalCount", prescriptionService.getTotalPrescriptionsCount());
        model.addAttribute("activeNav", "prescriptions");
        return "prescriptions/list";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(value = "patientId", required = false) Long patientId,
                                 @RequestParam(value = "doctorId", required = false) Long doctorId,
                                 @RequestParam(value = "appointmentId", required = false) Long appointmentId,
                                 Model model) {
        PrescriptionForm form = new PrescriptionForm();
        form.setPrescriptionCode(prescriptionService.generateNextPrescriptionCode());
        form.setPrescriptionDate(LocalDate.now());

        if (appointmentId != null) {
            appointmentService.findById(appointmentId).ifPresent(apt -> {
                form.setAppointmentId(apt.getId());
                form.setPatientId(apt.getPatient().getId());
                form.setDoctorId(apt.getDoctor().getId());
                form.setDiagnosis("Follow-up for: " + apt.getChiefComplaint());
            });
        } else {
            if (patientId != null) form.setPatientId(patientId);
            if (doctorId != null) form.setDoctorId(doctorId);
        }

        model.addAttribute("form", form);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("activeNav", "prescriptions");
        return "prescriptions/form";
    }

    @PostMapping("/save")
    public String savePrescription(@Valid @ModelAttribute("form") PrescriptionForm form,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        // Validate patient & doctor presence (PDF rule: "Rejected if the patient or doctor ID does not exist.")
        if (form.getPatientId() == null || !patientService.findById(form.getPatientId()).isPresent()) {
            bindingResult.rejectValue("patientId", "notFound", "Prescription Rejected: Patient does not exist.");
        }
        if (form.getDoctorId() == null || !doctorService.findById(form.getDoctorId()).isPresent()) {
            bindingResult.rejectValue("doctorId", "notFound", "Prescription Rejected: Doctor does not exist.");
        }

        // Validate at least one non-empty medicine and validate its dosage/duration
        boolean hasValidMedicine = false;
        if (form.getItems() != null) {
            for (int i = 0; i < form.getItems().size(); i++) {
                PrescriptionItem item = form.getItems().get(i);
                if (item != null && item.getMedicineName() != null && !item.getMedicineName().trim().isEmpty()) {
                    hasValidMedicine = true;
                    if (item.getDosage() == null || item.getDosage().trim().isEmpty()) {
                        bindingResult.rejectValue("items[" + i + "].dosage", "required", "Dosage is required for " + item.getMedicineName() + ".");
                    }
                    if (item.getDuration() == null || item.getDuration().trim().isEmpty()) {
                        bindingResult.rejectValue("items[" + i + "].duration", "required", "Duration is required for " + item.getMedicineName() + ".");
                    }
                }
            }
        }
        if (!hasValidMedicine) {
            bindingResult.rejectValue("items", "empty", "Please add at least one medicine with a name, dosage, and duration.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("activeNav", "prescriptions");
            return "prescriptions/form";
        }

        Prescription prescription = new Prescription();
        prescription.setPrescriptionCode(form.getPrescriptionCode());
        prescription.setPrescriptionDate(form.getPrescriptionDate());
        prescription.setDiagnosis(form.getDiagnosis());
        prescription.setAdvice(form.getAdvice());

        if (form.getAppointmentId() != null) {
            appointmentService.findById(form.getAppointmentId()).ifPresent(prescription::setAppointment);
        }

        Prescription saved = prescriptionService.createPrescription(form.getPatientId(), form.getDoctorId(), prescription, form.getItems());
        redirectAttributes.addFlashAttribute("successMessage", "Prescription " + saved.getPrescriptionCode() + " created successfully.");
        return "redirect:/prescriptions/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String viewPrescription(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return prescriptionService.findById(id).map(prescription -> {
            model.addAttribute("prescription", prescription);
            model.addAttribute("hospitalName", hospitalName);
            model.addAttribute("hospitalTagline", hospitalTagline);
            model.addAttribute("hospitalAddress", hospitalAddress);
            model.addAttribute("hospitalPhone", hospitalPhone);
            model.addAttribute("activeNav", "prescriptions");
            return "prescriptions/view";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Prescription not found with ID #" + id);
            return "redirect:/prescriptions";
        });
    }

    @PostMapping("/{id}/delete")
    public String deletePrescription(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            prescriptionService.deletePrescription(id);
            redirectAttributes.addFlashAttribute("successMessage", "Prescription record deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting prescription: " + e.getMessage());
        }
        return "redirect:/prescriptions";
    }
}
