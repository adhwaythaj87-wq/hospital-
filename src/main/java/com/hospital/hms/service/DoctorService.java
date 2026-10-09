package com.hospital.hms.service;

import com.hospital.hms.model.Doctor;
import com.hospital.hms.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAllByOrderByFullNameAsc();
    }

    public List<Doctor> searchDoctors(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllDoctors();
        }
        return doctorRepository.searchDoctors(query.trim());
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        if (specialization == null || specialization.trim().isEmpty()) {
            return getAllDoctors();
        }
        return doctorRepository.findBySpecializationIgnoreCaseOrderByFullNameAsc(specialization.trim());
    }

    public List<String> getAllSpecializations() {
        return doctorRepository.findDistinctSpecializations();
    }

    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id);
    }

    public Optional<Doctor> findByDoctorCode(String doctorCode) {
        if (doctorCode == null) return Optional.empty();
        return doctorRepository.findByDoctorCodeIgnoreCase(doctorCode.trim());
    }

    public boolean existsByDoctorCode(String doctorCode) {
        if (doctorCode == null) return false;
        return doctorRepository.existsByDoctorCodeIgnoreCase(doctorCode.trim());
    }

    public Doctor saveDoctor(Doctor doctor) {
        if (doctor.getDoctorCode() != null) {
            doctor.setDoctorCode(doctor.getDoctorCode().trim().toUpperCase());
        }
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    public long getTotalDoctorsCount() {
        return doctorRepository.count();
    }

    public String generateNextDoctorCode() {
        long count = doctorRepository.count() + 101;
        String code = String.format("D-%03d", count);
        while (doctorRepository.existsByDoctorCodeIgnoreCase(code)) {
            count++;
            code = String.format("D-%03d", count);
        }
        return code;
    }
}
