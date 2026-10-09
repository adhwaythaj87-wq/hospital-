package com.hospital.hms.service;

import com.hospital.hms.model.Appointment;
import com.hospital.hms.model.Bill;
import com.hospital.hms.model.Patient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PrescriptionService prescriptionService;
    private final BillingService billingService;

    public DashboardService(PatientService patientService,
                            DoctorService doctorService,
                            AppointmentService appointmentService,
                            PrescriptionService prescriptionService,
                            BillingService billingService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.prescriptionService = prescriptionService;
        this.billingService = billingService;
    }

    public Map<String, Object> getDashboardData() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalPatients", patientService.getTotalPatientsCount());
        stats.put("totalDoctors", doctorService.getTotalDoctorsCount());
        stats.put("totalAppointments", appointmentService.getTotalAppointmentsCount());
        stats.put("scheduledAppointments", appointmentService.getScheduledCount());
        stats.put("todayAppointments", appointmentService.getTodayCount());
        stats.put("totalPrescriptions", prescriptionService.getTotalPrescriptionsCount());
        stats.put("totalBills", billingService.getTotalBillsCount());
        stats.put("pendingBills", billingService.getPendingBillsCount());
        stats.put("totalRevenue", billingService.getTotalRevenue());
        stats.put("pendingRevenue", billingService.getPendingRevenue());

        List<Appointment> recentAppointments = appointmentService.getAllAppointments();
        if (recentAppointments.size() > 5) {
            recentAppointments = recentAppointments.subList(0, 5);
        }
        stats.put("recentAppointments", recentAppointments);

        List<Bill> recentBills = billingService.getAllBills();
        if (recentBills.size() > 5) {
            recentBills = recentBills.subList(0, 5);
        }
        stats.put("recentBills", recentBills);

        List<Patient> recentPatients = patientService.getAllPatients();
        if (recentPatients.size() > 5) {
            recentPatients = recentPatients.subList(0, 5);
        }
        stats.put("recentPatients", recentPatients);

        return stats;
    }
}
