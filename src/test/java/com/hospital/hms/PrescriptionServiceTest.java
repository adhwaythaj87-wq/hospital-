package com.hospital.hms;

import com.hospital.hms.model.Doctor;
import com.hospital.hms.model.Patient;
import com.hospital.hms.model.Prescription;
import com.hospital.hms.model.PrescriptionItem;
import com.hospital.hms.service.DoctorService;
import com.hospital.hms.service.PatientService;
import com.hospital.hms.service.PrescriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PrescriptionServiceTest {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Test
    void testCreatePrescriptionSuccess() {
        Patient patient = patientService.getAllPatients().get(0);
        Doctor doctor = doctorService.getAllDoctors().get(0);

        Prescription rx = new Prescription();
        rx.setPrescriptionDate(LocalDate.now());
        rx.setDiagnosis("Seasonal Allergies");
        rx.setAdvice("Drink warm water and get plenty of rest.");

        List<PrescriptionItem> items = List.of(
                new PrescriptionItem("Cetirizine 10mg", "1 Tab", "0-0-1", "5 Days", "Bedtime"),
                new PrescriptionItem("Vitamin C 500mg", "1 Tab", "1-0-0", "10 Days", "After breakfast")
        );

        Prescription saved = prescriptionService.createPrescription(patient.getId(), doctor.getId(), rx, items);

        assertNotNull(saved.getId());
        assertNotNull(saved.getPrescriptionCode());
        assertEquals(2, saved.getItems().size());
        assertEquals(patient.getId(), saved.getPatient().getId());
        assertEquals(doctor.getId(), saved.getDoctor().getId());
    }

    @Test
    void testRejectPrescriptionWhenPatientDoesNotExist() {
        Doctor doctor = doctorService.getAllDoctors().get(0);
        Prescription rx = new Prescription();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            prescriptionService.createPrescription(999999L, doctor.getId(), rx, List.of());
        });

        assertTrue(ex.getMessage().contains("Prescription Rejected: Patient ID"));
    }

    @Test
    void testRejectPrescriptionWhenDoctorDoesNotExist() {
        Patient patient = patientService.getAllPatients().get(0);
        Prescription rx = new Prescription();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            prescriptionService.createPrescription(patient.getId(), 888888L, rx, List.of());
        });

        assertTrue(ex.getMessage().contains("Prescription Rejected: Doctor ID"));
    }

    @Test
    void testUpdatePrescriptionItemsSafely() {
        Patient patient = patientService.getAllPatients().get(0);
        Doctor doctor = doctorService.getAllDoctors().get(0);

        Prescription rx = new Prescription();
        rx.setPrescriptionDate(LocalDate.now());
        rx.setDiagnosis("Initial Diagnosis");

        List<PrescriptionItem> items = List.of(
                new PrescriptionItem("Paracetamol 650mg", "1 Tab", "1-0-1", "3 Days", "After food")
        );

        Prescription saved = prescriptionService.createPrescription(patient.getId(), doctor.getId(), rx, items);
        assertNotNull(saved.getId());

        // Update items using setItems
        List<PrescriptionItem> newItems = List.of(
                new PrescriptionItem("Ibuprofen 400mg", "1 Tab", "1-0-0", "2 Days", "With food"),
                new PrescriptionItem("Antacid Gel", "10ml", "0-0-1", "5 Days", "Bedtime")
        );
        saved.setItems(newItems);
        Prescription updated = prescriptionService.savePrescription(saved);

        assertEquals(2, updated.getItems().size());
        assertEquals("Ibuprofen 400mg", updated.getItems().get(0).getMedicineName());
    }

    @Test
    void testSearchPrescriptions() {
        List<Prescription> results = prescriptionService.searchPrescriptions("URTI");
        assertNotNull(results);

        List<Prescription> emptySearch = prescriptionService.searchPrescriptions(null);
        assertEquals(prescriptionService.getAllPrescriptions().size(), emptySearch.size());
    }
}
