package com.hospital.hms.controller;

import com.hospital.hms.model.Appointment;
import com.hospital.hms.model.AppointmentStatus;
import com.hospital.hms.service.AppointmentService;
import com.hospital.hms.service.DoctorService;
import com.hospital.hms.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public AppointmentController(AppointmentService appointmentService,
                                 PatientService patientService,
                                 DoctorService doctorService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @GetMapping
    public String listAppointments(@RequestParam(value = "search", required = false) String search,
                                   @RequestParam(value = "status", required = false) AppointmentStatus status,
                                   Model model) {
        List<Appointment> appointments;
        if (search != null && !search.trim().isEmpty()) {
            appointments = appointmentService.searchAppointments(search);
        } else if (status != null) {
            appointments = appointmentService.getAppointmentsByStatus(status);
        } else {
            appointments = appointmentService.getAllAppointments();
        }

        model.addAttribute("appointments", appointments);
        model.addAttribute("search", search);
        model.addAttribute("currentStatus", status);
        model.addAttribute("statuses", AppointmentStatus.values());
        model.addAttribute("totalCount", appointmentService.getTotalAppointmentsCount());
        model.addAttribute("scheduledCount", appointmentService.getScheduledCount());
        model.addAttribute("todayCount", appointmentService.getTodayCount());
        model.addAttribute("activeNav", "appointments");
        return "appointments/list";
    }

    @GetMapping("/new")
    public String showBookForm(@RequestParam(value = "patientId", required = false) Long patientId,
                               @RequestParam(value = "doctorId", required = false) Long doctorId,
                               Model model) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentCode(appointmentService.generateNextAppointmentCode());
        appointment.setAppointmentDate(LocalDate.now());
        appointment.setAppointmentTime(LocalTime.of(10, 0));
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        if (patientId != null) {
            patientService.findById(patientId).ifPresent(appointment::setPatient);
        }
        if (doctorId != null) {
            doctorService.findById(doctorId).ifPresent(appointment::setDoctor);
        }

        model.addAttribute("appointment", appointment);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("statuses", AppointmentStatus.values());
        model.addAttribute("activeNav", "appointments");
        return "appointments/form";
    }

    @PostMapping("/save")
    public String saveAppointment(@Valid @ModelAttribute("appointment") Appointment appointment,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (appointment.getPatient() == null || appointment.getPatient().getId() == null ||
                !patientService.findById(appointment.getPatient().getId()).isPresent()) {
            bindingResult.rejectValue("patient", "required", "Please select a valid registered patient.");
        }
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null ||
                !doctorService.findById(appointment.getDoctor().getId()).isPresent()) {
            bindingResult.rejectValue("doctor", "required", "Please select a valid doctor.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("statuses", AppointmentStatus.values());
            model.addAttribute("activeNav", "appointments");
            return "appointments/form";
        }

        // Fetch managed entities
        appointment.setPatient(patientService.findById(appointment.getPatient().getId()).get());
        appointment.setDoctor(doctorService.findById(appointment.getDoctor().getId()).get());

        appointmentService.saveAppointment(appointment);
        redirectAttributes.addFlashAttribute("successMessage", "Appointment " + appointment.getAppointmentCode() + " booked successfully.");
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("status") AppointmentStatus status,
                               RedirectAttributes redirectAttributes) {
        try {
            appointmentService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment status updated to " + status.getDisplayName() + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update appointment status: " + e.getMessage());
        }
        return "redirect:/appointments";
    }

    @PostMapping("/{id}/delete")
    public String deleteAppointment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.deleteAppointment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting appointment: " + e.getMessage());
        }
        return "redirect:/appointments";
    }
}
