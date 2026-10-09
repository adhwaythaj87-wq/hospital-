package com.hospital.hms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_code", unique = true, nullable = false, length = 30)
    private String billCode;

    @NotNull(message = "Patient is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @NotNull(message = "Bill date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Consultation fee cannot be negative")
    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee = new BigDecimal("500.00");

    @NotNull(message = "Medicine charge is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Medicine charge cannot be negative")
    @Column(name = "medicine_charge", nullable = false, precision = 10, scale = 2)
    private BigDecimal medicineCharge = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Lab/diagnostics charge cannot be negative")
    @Column(name = "lab_charges", precision = 10, scale = 2)
    private BigDecimal labCharges = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount cannot be negative")
    @Column(name = "discount", precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax cannot be negative")
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total amount cannot be negative")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @DecimalMin(value = "0.0", inclusive = true, message = "Paid amount cannot be negative")
    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Bill() {
    }

    public Bill(String billCode, Patient patient, Doctor doctor, LocalDate billDate, BigDecimal consultationFee, BigDecimal medicineCharge) {
        this.billCode = billCode;
        this.patient = patient;
        this.doctor = doctor;
        this.billDate = billDate;
        this.consultationFee = consultationFee != null ? consultationFee : BigDecimal.ZERO;
        this.medicineCharge = medicineCharge != null ? medicineCharge : BigDecimal.ZERO;
        this.calculateTotal();
    }

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.billDate == null) {
            this.billDate = LocalDate.now();
        }
        if (this.consultationFee == null) this.consultationFee = BigDecimal.ZERO;
        if (this.medicineCharge == null) this.medicineCharge = BigDecimal.ZERO;
        if (this.labCharges == null) this.labCharges = BigDecimal.ZERO;
        if (this.discount == null) this.discount = BigDecimal.ZERO;
        if (this.taxAmount == null) this.taxAmount = BigDecimal.ZERO;
        if (this.paidAmount == null) this.paidAmount = BigDecimal.ZERO;

        BigDecimal subtotal = this.consultationFee.add(this.medicineCharge).add(this.labCharges);
        BigDecimal afterDiscount = subtotal.subtract(this.discount);
        if (afterDiscount.compareTo(BigDecimal.ZERO) < 0) {
            afterDiscount = BigDecimal.ZERO;
        }
        this.totalAmount = afterDiscount.add(this.taxAmount).setScale(2, RoundingMode.HALF_UP);
    }

    // Helper to calculate balance due
    public BigDecimal getBalanceDue() {
        if (this.totalAmount == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal paid = this.paidAmount != null ? this.paidAmount : BigDecimal.ZERO;
        BigDecimal balance = this.totalAmount.subtract(paid);
        return balance.compareTo(BigDecimal.ZERO) > 0 ? balance.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public BigDecimal getMedicineCharge() {
        return medicineCharge;
    }

    public void setMedicineCharge(BigDecimal medicineCharge) {
        this.medicineCharge = medicineCharge;
    }

    public BigDecimal getLabCharges() {
        return labCharges;
    }

    public void setLabCharges(BigDecimal labCharges) {
        this.labCharges = labCharges;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
