package com.hospital.hms.service;

import com.hospital.hms.model.Patient;
import com.hospital.hms.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAllByOrderByIdDesc();
    }

    public List<Patient> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPatients();
        }
        return patientRepository.searchPatients(query.trim());
    }

    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    public Optional<Patient> findByPatientCode(String patientCode) {
        if (patientCode == null) return Optional.empty();
        return patientRepository.findByPatientCodeIgnoreCase(patientCode.trim());
    }

    public boolean existsByPatientCode(String patientCode) {
        if (patientCode == null) return false;
        return patientRepository.existsByPatientCodeIgnoreCase(patientCode.trim());
    }

    public Patient savePatient(Patient patient) {
        if (patient.getPatientCode() != null) {
            patient.setPatientCode(patient.getPatientCode().trim().toUpperCase());
        }
        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    public long getTotalPatientsCount() {
        return patientRepository.count();
    }

    public String generateNextPatientCode() {
        long count = patientRepository.count() + 1;
        String code = String.format("P-%04d", count);
        while (patientRepository.existsByPatientCodeIgnoreCase(code)) {
            count++;
            code = String.format("P-%04d", count);
        }
        return code;
    }
}
