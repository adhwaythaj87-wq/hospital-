package com.hospital.hms.repository;

import com.hospital.hms.model.Doctor;
import com.hospital.hms.model.Patient;
import com.hospital.hms.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByPrescriptionCodeIgnoreCase(String prescriptionCode);

    List<Prescription> findByPatientOrderByPrescriptionDateDesc(Patient patient);

    List<Prescription> findByDoctorOrderByPrescriptionDateDesc(Doctor doctor);

    List<Prescription> findAllByOrderByPrescriptionDateDesc();

    @Query("SELECT pr FROM Prescription pr WHERE " +
           "LOWER(pr.prescriptionCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(pr.patient.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(pr.patient.patientCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(pr.doctor.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(pr.diagnosis) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY pr.prescriptionDate DESC")
    List<Prescription> searchPrescriptions(@Param("query") String query);
}
