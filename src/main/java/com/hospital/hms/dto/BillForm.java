package com.hospital.hms.dto;

import com.hospital.hms.model.PaymentMethod;
import com.hospital.hms.model.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BillForm {

    private Long id;
    private String billCode;

    @NotNull(message = "Please select a patient")
    private Long patientId;

    private Long doctorId;
    private Long appointmentId;
    private Long prescriptionId;

    @NotNull(message = "Bill date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate billDate = LocalDate.now();

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Consultation fee cannot be negative")
    private BigDecimal consultationFee = new BigDecimal("500.00");

    @NotNull(message = "Medicine charge is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Medicine charge cannot be negative")
    private BigDecimal medicineCharge = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Lab charge cannot be negative")
    private BigDecimal labCharges = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount cannot be negative")
    private BigDecimal discount = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax cannot be negative")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    private BigDecimal paidAmount = BigDecimal.ZERO;
    private String notes;

    public BillForm() {
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

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
