package com.hospital.hms.controller;

import com.hospital.hms.dto.BillForm;
import com.hospital.hms.model.*;
import com.hospital.hms.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;

    @Value("${hospital.name:LifeCare Multispeciality Hospital}")
    private String hospitalName;

    @Value("${hospital.tagline:Compassionate Care, Clinical Excellence}")
    private String hospitalTagline;

    @Value("${hospital.address:100 Healthcare Avenue, Medical District, Kochi}")
    private String hospitalAddress;

    @Value("${hospital.phone:+91 (0484) 285-4000}")
    private String hospitalPhone;

    public BillingController(BillingService billingService,
                             PatientService patientService,
                             DoctorService doctorService,
                             AppointmentService appointmentService,
                             PrescriptionService prescriptionService) {
        this.billingService = billingService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public String listBills(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "status", required = false) PaymentStatus status,
                            Model model) {
        List<Bill> bills;
        if (search != null && !search.trim().isEmpty()) {
            bills = billingService.searchBills(search);
        } else if (status != null) {
            bills = billingService.getBillsByStatus(status);
        } else {
            bills = billingService.getAllBills();
        }

        model.addAttribute("bills", bills);
        model.addAttribute("search", search);
        model.addAttribute("currentStatus", status);
        model.addAttribute("statuses", PaymentStatus.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("totalCount", billingService.getTotalBillsCount());
        model.addAttribute("pendingCount", billingService.getPendingBillsCount());
        model.addAttribute("totalRevenue", billingService.getTotalRevenue());
        model.addAttribute("pendingRevenue", billingService.getPendingRevenue());
        model.addAttribute("activeNav", "billing");
        return "billing/list";
    }

    @GetMapping("/new")
    public String showGenerateBillForm(@RequestParam(value = "patientId", required = false) Long patientId,
                                       @RequestParam(value = "doctorId", required = false) Long doctorId,
                                       @RequestParam(value = "appointmentId", required = false) Long appointmentId,
                                       @RequestParam(value = "prescriptionId", required = false) Long prescriptionId,
                                       Model model) {
        BillForm form = new BillForm();
        form.setBillCode(billingService.generateNextBillCode());
        form.setBillDate(LocalDate.now());
        form.setConsultationFee(billingService.getDefaultConsultationFee());
        form.setMedicineCharge(BigDecimal.ZERO);
        form.setLabCharges(BigDecimal.ZERO);
        form.setDiscount(BigDecimal.ZERO);
        form.setTaxAmount(BigDecimal.ZERO);
        form.setPaymentStatus(PaymentStatus.PENDING);
        form.setPaymentMethod(PaymentMethod.CASH);

        if (patientId != null) form.setPatientId(patientId);
        if (doctorId != null) {
            form.setDoctorId(doctorId);
            doctorService.findById(doctorId).ifPresent(doc -> {
                if (doc.getConsultationFee() != null) {
                    form.setConsultationFee(doc.getConsultationFee());
                }
            });
        }
        if (appointmentId != null) {
            form.setAppointmentId(appointmentId);
            appointmentService.findById(appointmentId).ifPresent(apt -> {
                form.setPatientId(apt.getPatient().getId());
                form.setDoctorId(apt.getDoctor().getId());
                if (apt.getDoctor().getConsultationFee() != null) {
                    form.setConsultationFee(apt.getDoctor().getConsultationFee());
                }
            });
        }
        if (prescriptionId != null) {
            form.setPrescriptionId(prescriptionId);
            prescriptionService.findById(prescriptionId).ifPresent(rx -> {
                form.setPatientId(rx.getPatient().getId());
                form.setDoctorId(rx.getDoctor().getId());
                if (rx.getDoctor().getConsultationFee() != null) {
                    form.setConsultationFee(rx.getDoctor().getConsultationFee());
                }
                // Estimate default medicine charge based on item count
                if (rx.getItems() != null && !rx.getItems().isEmpty()) {
                    form.setMedicineCharge(new BigDecimal(rx.getItems().size() * 150));
                }
            });
        }

        model.addAttribute("form", form);
        model.addAttribute("patients", patientService.getAllPatients());
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("paymentStatuses", PaymentStatus.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("activeNav", "billing");
        return "billing/form";
    }

    @PostMapping("/save")
    public String saveBill(@Valid @ModelAttribute("form") BillForm form,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (form.getPatientId() == null || !patientService.findById(form.getPatientId()).isPresent()) {
            bindingResult.rejectValue("patientId", "notFound", "Please select a valid patient.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("doctors", doctorService.getAllDoctors());
            model.addAttribute("paymentStatuses", PaymentStatus.values());
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("activeNav", "billing");
            return "billing/form";
        }

        Bill bill = new Bill();
        bill.setBillCode(form.getBillCode());
        bill.setBillDate(form.getBillDate());
        bill.setConsultationFee(form.getConsultationFee());
        bill.setMedicineCharge(form.getMedicineCharge());
        bill.setLabCharges(form.getLabCharges());
        bill.setDiscount(form.getDiscount());
        bill.setTaxAmount(form.getTaxAmount());
        bill.setPaymentStatus(form.getPaymentStatus());
        bill.setPaymentMethod(form.getPaymentMethod());
        bill.setPaidAmount(form.getPaidAmount());
        bill.setNotes(form.getNotes());

        patientService.findById(form.getPatientId()).ifPresent(bill::setPatient);
        if (form.getDoctorId() != null) {
            doctorService.findById(form.getDoctorId()).ifPresent(bill::setDoctor);
        }
        if (form.getAppointmentId() != null) {
            appointmentService.findById(form.getAppointmentId()).ifPresent(bill::setAppointment);
        }
        if (form.getPrescriptionId() != null) {
            prescriptionService.findById(form.getPrescriptionId()).ifPresent(bill::setPrescription);
        }

        bill.calculateTotal();
        if (form.getPaymentStatus() == PaymentStatus.PAID && (bill.getPaidAmount() == null || bill.getPaidAmount().compareTo(BigDecimal.ZERO) == 0)) {
            bill.setPaidAmount(bill.getTotalAmount());
        }

        Bill saved = billingService.generateBill(bill);
        redirectAttributes.addFlashAttribute("successMessage", "Invoice " + saved.getBillCode() + " generated successfully.");
        return "redirect:/billing/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return billingService.findById(id).map(bill -> {
            model.addAttribute("bill", bill);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("hospitalName", hospitalName);
            model.addAttribute("hospitalTagline", hospitalTagline);
            model.addAttribute("hospitalAddress", hospitalAddress);
            model.addAttribute("hospitalPhone", hospitalPhone);
            model.addAttribute("activeNav", "billing");
            return "billing/invoice";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Bill not found with ID #" + id);
            return "redirect:/billing";
        });
    }

    @PostMapping("/{id}/pay")
    public String markAsPaid(@PathVariable("id") Long id,
                             @RequestParam(value = "paymentMethod", defaultValue = "CASH") PaymentMethod method,
                             RedirectAttributes redirectAttributes) {
        try {
            billingService.markAsPaid(id, method);
            redirectAttributes.addFlashAttribute("successMessage", "Payment recorded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update payment: " + e.getMessage());
        }
        return "redirect:/billing/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteBill(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            billingService.deleteBill(id);
            redirectAttributes.addFlashAttribute("successMessage", "Invoice deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting bill: " + e.getMessage());
        }
        return "redirect:/billing";
    }
}
