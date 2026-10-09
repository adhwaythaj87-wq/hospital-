package com.hospital.hms.service;

import com.hospital.hms.model.Bill;
import com.hospital.hms.model.Patient;
import com.hospital.hms.model.PaymentMethod;
import com.hospital.hms.model.PaymentStatus;
import com.hospital.hms.repository.BillRepository;
import com.hospital.hms.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BillingService {

    private final BillRepository billRepository;
    private final PatientRepository patientRepository;

    @Value("${hospital.consultation.default-fee:500.00}")
    private BigDecimal defaultConsultationFee;

    public BillingService(BillRepository billRepository, PatientRepository patientRepository) {
        this.billRepository = billRepository;
        this.patientRepository = patientRepository;
    }

    public List<Bill> getAllBills() {
        return billRepository.findAllByOrderByBillDateDesc();
    }

    public List<Bill> searchBills(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllBills();
        }
        return billRepository.searchBills(query.trim());
    }

    public List<Bill> getBillsByStatus(PaymentStatus status) {
        if (status == null) {
            return getAllBills();
        }
        return billRepository.findByPaymentStatusOrderByBillDateDesc(status);
    }

    public List<Bill> getBillsByPatient(Patient patient) {
        return billRepository.findByPatientOrderByBillDateDesc(patient);
    }

    public Optional<Bill> findById(Long id) {
        return billRepository.findById(id);
    }

    public Bill generateBill(Bill bill) {
        if (bill.getPatient() == null || bill.getPatient().getId() == null ||
                !patientRepository.existsById(bill.getPatient().getId())) {
            throw new IllegalArgumentException("Billing Error: Patient must exist to generate bill.");
        }

        if (bill.getBillCode() == null || bill.getBillCode().trim().isEmpty()) {
            bill.setBillCode(generateNextBillCode());
        }

        if (bill.getConsultationFee() == null) {
            bill.setConsultationFee(defaultConsultationFee != null ? defaultConsultationFee : new BigDecimal("500.00"));
        }

        if (bill.getMedicineCharge() == null) {
            bill.setMedicineCharge(BigDecimal.ZERO);
        }

        bill.calculateTotal();

        // Adjust payment status based on paid amount
        updatePaymentStatusFromAmounts(bill);

        return billRepository.save(bill);
    }

    public Bill recordPayment(Long billId, BigDecimal amountPaid, PaymentMethod method, String notes) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found with ID: " + billId));

        if (amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }

        BigDecimal currentPaid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaid = currentPaid.add(amountPaid);
        bill.setPaidAmount(newPaid);
        bill.setPaymentMethod(method != null ? method : PaymentMethod.CASH);
        bill.setPaidAt(LocalDateTime.now());
        if (notes != null && !notes.trim().isEmpty()) {
            bill.setNotes((bill.getNotes() != null ? bill.getNotes() + " | " : "") + notes.trim());
        }

        updatePaymentStatusFromAmounts(bill);
        return billRepository.save(bill);
    }

    public Bill markAsPaid(Long billId, PaymentMethod method) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found with ID: " + billId));

        bill.setPaidAmount(bill.getTotalAmount());
        bill.setPaymentStatus(PaymentStatus.PAID);
        bill.setPaymentMethod(method != null ? method : PaymentMethod.CASH);
        bill.setPaidAt(LocalDateTime.now());
        return billRepository.save(bill);
    }

    private void updatePaymentStatusFromAmounts(Bill bill) {
        BigDecimal paid = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal total = bill.getTotalAmount() != null ? bill.getTotalAmount() : BigDecimal.ZERO;

        if (paid.compareTo(BigDecimal.ZERO) == 0) {
            bill.setPaymentStatus(PaymentStatus.PENDING);
        } else if (paid.compareTo(total) >= 0) {
            bill.setPaymentStatus(PaymentStatus.PAID);
            if (bill.getPaidAt() == null) {
                bill.setPaidAt(LocalDateTime.now());
            }
        } else {
            bill.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        }
    }

    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }

    public long getTotalBillsCount() {
        return billRepository.count();
    }

    public long getPendingBillsCount() {
        return billRepository.countByPaymentStatus(PaymentStatus.PENDING);
    }

    public BigDecimal getTotalRevenue() {
        BigDecimal rev = billRepository.calculateTotalRevenue();
        return rev != null ? rev : BigDecimal.ZERO;
    }

    public BigDecimal getPendingRevenue() {
        BigDecimal pending = billRepository.calculatePendingRevenue(PaymentStatus.PAID);
        return pending != null ? pending : BigDecimal.ZERO;
    }

    public String generateNextBillCode() {
        long count = billRepository.count() + 1001;
        String code = String.format("INV-%04d", count);
        while (billRepository.findByBillCodeIgnoreCase(code).isPresent()) {
            count++;
            code = String.format("INV-%04d", count);
        }
        return code;
    }

    public BigDecimal getDefaultConsultationFee() {
        return defaultConsultationFee != null ? defaultConsultationFee : new BigDecimal("500.00");
    }
}
