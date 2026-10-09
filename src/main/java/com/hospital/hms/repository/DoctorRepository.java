package com.hospital.hms.repository;

import com.hospital.hms.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByDoctorCodeIgnoreCase(String doctorCode);

    boolean existsByDoctorCodeIgnoreCase(String doctorCode);

    @Query("SELECT d FROM Doctor d WHERE " +
           "LOWER(d.doctorCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "d.phone LIKE CONCAT('%', :query, '%') OR " +
           "(d.qualification IS NOT NULL AND LOWER(d.qualification) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "(d.roomNumber IS NOT NULL AND LOWER(d.roomNumber) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY d.fullName ASC")
    List<Doctor> searchDoctors(@Param("query") String query);

    List<Doctor> findAllByOrderByFullNameAsc();

    List<Doctor> findBySpecializationIgnoreCaseOrderByFullNameAsc(String specialization);

    @Query("SELECT DISTINCT d.specialization FROM Doctor d ORDER BY d.specialization ASC")
    List<String> findDistinctSpecializations();
}
