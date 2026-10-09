package com.hospital.hms.repository;

import com.hospital.hms.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientCodeIgnoreCase(String patientCode);

    boolean existsByPatientCodeIgnoreCase(String patientCode);

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.patientCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "p.phone LIKE CONCAT('%', :query, '%') OR " +
           "(p.email IS NOT NULL AND LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY p.id DESC")
    List<Patient> searchPatients(@Param("query") String query);

    List<Patient> findAllByOrderByIdDesc();
}
