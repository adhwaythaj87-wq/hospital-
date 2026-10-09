package com.hospital.hms;

import com.hospital.hms.model.*;
import com.hospital.hms.service.BillingService;
import com.hospital.hms.service.DoctorService;
import com.hospital.hms.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BillingServiceTest {

    @Autowired
    private BillingService billingService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Test
    void testGenerateBillCalculation() {
        Patient patient = patientService.getAllPatients().get(0);
        Doctor doctor = doctorService.getAllDoctors().get(0);

        Bill bill = new Bill();
        bill.setPatient(patient);
        bill.setDoctor(doctor);
        bill.setBillDate(LocalDate.now());
        bill.setConsultationFee(new BigDecimal("500.00"));
        bill.setMedicineCharge(new BigDecimal("350.00"));
        bill.setLabCharges(new BigDecimal("150.00"));
        bill.setDiscount(new BigDecimal("50.00"));
        bill.setTaxAmount(new BigDecimal("20.00"));
        // Total should be (500 + 350 + 150) - 50 + 20 = 970.00

        Bill saved = billingService.generateBill(bill);
        assertNotNull(saved.getId());
        assertNotNull(saved.getBillCode());
        assertEquals(new BigDecimal("970.00"), saved.getTotalAmount());
        assertEquals(PaymentStatus.PENDING, saved.getPaymentStatus());
        assertEquals(new BigDecimal("970.00"), saved.getBalanceDue());
    }

    @Test
    void testRecordPaymentToPaid() {
        Patient patient = patientService.getAllPatients().get(0);

        Bill bill = new Bill();
        bill.setPatient(patient);
        bill.setBillDate(LocalDate.now());
        bill.setConsultationFee(new BigDecimal("500.00"));
        bill.setMedicineCharge(new BigDecimal("200.00"));

        Bill saved = billingService.generateBill(bill);
        assertEquals(new BigDecimal("700.00"), saved.getTotalAmount());

        Bill paidBill = billingService.markAsPaid(saved.getId(), PaymentMethod.UPI);
        assertEquals(PaymentStatus.PAID, paidBill.getPaymentStatus());
        assertEquals(new BigDecimal("700.00"), paidBill.getPaidAmount());
        assertEquals(new BigDecimal("0.00"), paidBill.getBalanceDue());
    }

    @Test
    void testRecordPartialPayment() {
        Patient patient = patientService.getAllPatients().get(0);

        Bill bill = new Bill();
        bill.setPatient(patient);
        bill.setBillDate(LocalDate.now());
        bill.setConsultationFee(new BigDecimal("1000.00"));

        Bill saved = billingService.generateBill(bill);
        assertEquals(new BigDecimal("1000.00"), saved.getTotalAmount());
        assertEquals(PaymentStatus.PENDING, saved.getPaymentStatus());

        Bill partiallyPaid = billingService.recordPayment(saved.getId(), new BigDecimal("400.00"), PaymentMethod.CASH, "Partial cash advance");
        assertEquals(PaymentStatus.PARTIALLY_PAID, partiallyPaid.getPaymentStatus());
        assertEquals(new BigDecimal("400.00"), partiallyPaid.getPaidAmount());
        assertEquals(new BigDecimal("600.00"), partiallyPaid.getBalanceDue());
    }

    @Test
    void testDiscountExceedingSubtotalFloorsAtZero() {
        Patient patient = patientService.getAllPatients().get(0);

        Bill bill = new Bill();
        bill.setPatient(patient);
        bill.setBillDate(LocalDate.now());
        bill.setConsultationFee(new BigDecimal("300.00"));
        bill.setDiscount(new BigDecimal("500.00")); // discount exceeds subtotal
        bill.setTaxAmount(new BigDecimal("10.00"));

        Bill saved = billingService.generateBill(bill);
        // (300 - 500) floored to 0 + 10 tax = 10.00
        assertEquals(new BigDecimal("10.00"), saved.getTotalAmount());
    }

    @Test
    void testRevenueMetricsAccuracy() {
        BigDecimal totalRev = billingService.getTotalRevenue();
        assertNotNull(totalRev);
        assertTrue(totalRev.compareTo(BigDecimal.ZERO) >= 0);

        BigDecimal pendingRev = billingService.getPendingRevenue();
        assertNotNull(pendingRev);
        assertTrue(pendingRev.compareTo(BigDecimal.ZERO) >= 0);
    }
}
