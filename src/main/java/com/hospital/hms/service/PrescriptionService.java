package com.hospital.hms.service;

import com.hospital.hms.model.Doctor;
import com.hospital.hms.model.Patient;
import com.hospital.hms.model.Prescription;
import com.hospital.hms.model.PrescriptionItem;
import com.hospital.hms.repository.DoctorRepository;
import com.hospital.hms.repository.PatientRepository;
import com.hospital.hms.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAllByOrderByPrescriptionDateDesc();
    }

    public List<Prescription> searchPrescriptions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPrescriptions();
        }
        return prescriptionRepository.searchPrescriptions(query.trim());
    }

    public Optional<Prescription> findById(Long id) {
        return prescriptionRepository.findById(id);
    }

    public List<Prescription> getPrescriptionsByPatient(Patient patient) {
        return prescriptionRepository.findByPatientOrderByPrescriptionDateDesc(patient);
    }

    public List<Prescription> getPrescriptionsByDoctor(Doctor doctor) {
        return prescriptionRepository.findByDoctorOrderByPrescriptionDateDesc(doctor);
    }

    public Prescription createPrescription(Long patientId, Long doctorId, Prescription prescription, List<PrescriptionItem> items) {
        // PDF Rule: "Rejected if the patient or doctor ID does not exist."
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription Rejected: Patient ID #" + patientId + " does not exist."));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription Rejected: Doctor ID #" + doctorId + " does not exist."));

        prescription.setPatient(patient);
        prescription.setDoctor(doctor);

        if (prescription.getPrescriptionCode() == null || prescription.getPrescriptionCode().trim().isEmpty()) {
            prescription.setPrescriptionCode(generateNextPrescriptionCode());
        }

        // Bind items
        if (items != null && !items.isEmpty()) {
            List<PrescriptionItem> validItems = new ArrayList<>();
            for (PrescriptionItem item : items) {
                if (item.getMedicineName() != null && !item.getMedicineName().trim().isEmpty()) {
                    item.setPrescription(prescription);
                    validItems.add(item);
                }
            }
            prescription.setItems(validItems);
        }

        return prescriptionRepository.save(prescription);
    }

    public Prescription savePrescription(Prescription prescription) {
        if (prescription.getPatient() == null || prescription.getPatient().getId() == null ||
                !patientRepository.existsById(prescription.getPatient().getId())) {
            throw new IllegalArgumentException("Prescription Rejected: Patient does not exist.");
        }
        if (prescription.getDoctor() == null || prescription.getDoctor().getId() == null ||
                !doctorRepository.existsById(prescription.getDoctor().getId())) {
            throw new IllegalArgumentException("Prescription Rejected: Doctor does not exist.");
        }
        if (prescription.getPrescriptionCode() == null || prescription.getPrescriptionCode().trim().isEmpty()) {
            prescription.setPrescriptionCode(generateNextPrescriptionCode());
        }
        if (prescription.getItems() != null) {
            for (PrescriptionItem item : prescription.getItems()) {
                item.setPrescription(prescription);
            }
        }
        return prescriptionRepository.save(prescription);
    }

    public void deletePrescription(Long id) {
        prescriptionRepository.deleteById(id);
    }

    public long getTotalPrescriptionsCount() {
        return prescriptionRepository.count();
    }

    public String generateNextPrescriptionCode() {
        long count = prescriptionRepository.count() + 1001;
        String code = String.format("RX-%04d", count);
        while (prescriptionRepository.findByPrescriptionCodeIgnoreCase(code).isPresent()) {
            count++;
            code = String.format("RX-%04d", count);
        }
        return code;
    }
}
