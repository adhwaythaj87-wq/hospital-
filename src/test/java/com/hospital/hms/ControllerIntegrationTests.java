package com.hospital.hms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testDashboardEndpoint() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/index"))
                .andExpect(content().string(containsString("Hospital Operations Overview")));
    }

    @Test
    void testPatientsListEndpoint() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/list"))
                .andExpect(content().string(containsString("Patient Directory")));
    }

    @Test
    void testPatientsNewFormEndpoint() throws Exception {
        mockMvc.perform(get("/patients/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/form"))
                .andExpect(content().string(containsString("Register New Patient")));
    }

    @Test
    void testDoctorsListEndpoint() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors/list"))
                .andExpect(content().string(containsString("Doctor Directory")));
    }

    @Test
    void testAppointmentsListEndpoint() throws Exception {
        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments/list"))
                .andExpect(content().string(containsString("Appointment Scheduling")));
    }

    @Test
    void testPrescriptionsListEndpoint() throws Exception {
        mockMvc.perform(get("/prescriptions"))
                .andExpect(status().isOk())
                .andExpect(view().name("prescriptions/list"))
                .andExpect(content().string(containsString("Prescription Management")));
    }

    @Test
    void testBillingListEndpoint() throws Exception {
        mockMvc.perform(get("/billing"))
                .andExpect(status().isOk())
                .andExpect(view().name("billing/list"))
                .andExpect(content().string(containsString("Hospital Billing & Accounts")));
    }

    @Test
    void testRecordsIndexEndpoint() throws Exception {
        mockMvc.perform(get("/records"))
                .andExpect(status().isOk())
                .andExpect(view().name("records/index"))
                .andExpect(content().string(containsString("Patient 360 Medical Records")));
    }

    @Test
    void testSavePatientSuccessAndRedirect() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/patients/save")
                        .param("patientCode", "P-8888")
                        .param("fullName", "Integration Patient")
                        .param("age", "29")
                        .param("gender", "Female")
                        .param("phone", "9876543219")
                        .param("bloodGroup", "B+"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testSavePatientValidationError() throws Exception {
        // Missing name and negative age
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/patients/save")
                        .param("patientCode", "P-7777")
                        .param("fullName", "")
                        .param("age", "-5")
                        .param("gender", "Male")
                        .param("phone", "123"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients/form"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testSaveAppointmentValidationErrorForMissingDoctor() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/appointments/save")
                        .param("appointmentCode", "APT-9999")
                        .param("patient.id", "999999") // invalid patient
                        .param("appointmentDate", "2026-10-10")
                        .param("appointmentTime", "10:00")
                        .param("chiefComplaint", "Headache"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments/form"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testPrescriptionRejectionForNonExistentPatient() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/prescriptions/save")
                        .param("prescriptionCode", "RX-9999")
                        .param("patientId", "999999") // non-existent
                        .param("doctorId", "1")
                        .param("prescriptionDate", "2026-10-10")
                        .param("items[0].medicineName", "Aspirin 100mg")
                        .param("items[0].dosage", "1 Tab")
                        .param("items[0].duration", "3 Days"))
                .andExpect(status().isOk())
                .andExpect(view().name("prescriptions/form"))
                .andExpect(model().attributeHasFieldErrors("form", "patientId"));
    }

    @Test
    void testGenerateBillAndMarkPaid() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/billing/save")
                        .param("billCode", "INV-9999")
                        .param("patientId", "1")
                        .param("consultationFee", "500.00")
                        .param("medicineCharge", "150.00")
                        .param("paymentStatus", "PAID"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testPatient360HistoryView() throws Exception {
        mockMvc.perform(get("/records/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("records/patient-history"))
                .andExpect(model().attributeExists("patient", "appointments", "prescriptions", "bills"));
    }

    @Test
    void testDoctorFilterBySpecialization() throws Exception {
        mockMvc.perform(get("/doctors").param("specialization", "Cardiology"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors/list"))
                .andExpect(content().string(containsString("Cardiology")));
    }

    @Test
    void testSaveDoctorSuccessAndRedirect() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/doctors/save")
                        .param("doctorCode", "D-888")
                        .param("fullName", "Dr. Test Integration")
                        .param("specialization", "Neurology")
                        .param("phone", "+91 9988776655")
                        .param("consultationFee", "650.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testSaveAppointmentSuccessAndRedirect() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/appointments/save")
                        .param("appointmentCode", "APT-8888")
                        .param("patient.id", "1")
                        .param("doctor.id", "1")
                        .param("appointmentDate", "2026-10-15")
                        .param("appointmentTime", "11:00")
                        .param("chiefComplaint", "Integration test appointment"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testSavePrescriptionSuccessAndRedirect() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/prescriptions/save")
                        .param("prescriptionCode", "RX-8888")
                        .param("patientId", "1")
                        .param("doctorId", "1")
                        .param("prescriptionDate", "2026-10-15")
                        .param("diagnosis", "Integration Test Diagnosis")
                        .param("items[0].medicineName", "Paracetamol 650mg")
                        .param("items[0].dosage", "1 Tablet")
                        .param("items[0].duration", "3 Days"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testFindByCodeEndpoint() throws Exception {
        mockMvc.perform(get("/records/by-code").param("code", "P-1001"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testMarkBillAsPaidEndpoint() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/billing/1/pay")
                        .param("paymentMethod", "UPI"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/billing/1"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}

