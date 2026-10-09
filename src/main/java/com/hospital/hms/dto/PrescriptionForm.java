package com.hospital.hms.dto;

import com.hospital.hms.model.PrescriptionItem;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionForm {

    private Long id;
    private String prescriptionCode;

    @NotNull(message = "Please select a patient")
    private Long patientId;

    @NotNull(message = "Please select a doctor")
    private Long doctorId;

    private Long appointmentId;

    @NotNull(message = "Prescription date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate prescriptionDate = LocalDate.now();

    private String diagnosis;
    private String advice;

    private List<PrescriptionItem> items = new ArrayList<>();

    public PrescriptionForm() {
        // Add one default empty item row for convenience
        items.add(new PrescriptionItem("", "", "1-0-1 (Twice Daily)", "5 Days", "After meals"));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrescriptionCode() {
        return prescriptionCode;
    }

    public void setPrescriptionCode(String prescriptionCode) {
        this.prescriptionCode = prescriptionCode;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public List<PrescriptionItem> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItem> items) {
        this.items = items;
    }
}
