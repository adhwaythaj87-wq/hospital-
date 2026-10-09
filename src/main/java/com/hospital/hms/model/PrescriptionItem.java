package com.hospital.hms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "prescription_items")
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @NotBlank(message = "Medicine name is required")
    @Column(name = "medicine_name", nullable = false, length = 150)
    private String medicineName;

    @NotBlank(message = "Dosage is required (e.g., 500mg)")
    @Column(nullable = false, length = 50)
    private String dosage;

    @Column(length = 50)
    private String frequency = "1-0-1 (Twice Daily)";

    @NotBlank(message = "Duration is required (e.g., 5 days)")
    @Column(nullable = false, length = 50)
    private String duration;

    @Column(length = 255)
    private String instructions = "After meals";

    public PrescriptionItem() {
    }

    public PrescriptionItem(String medicineName, String dosage, String frequency, String duration, String instructions) {
        this.medicineName = medicineName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
