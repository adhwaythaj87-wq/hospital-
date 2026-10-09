package com.hospital.hms.repository;

import com.hospital.hms.model.Appointment;
import com.hospital.hms.model.AppointmentStatus;
import com.hospital.hms.model.Doctor;
import com.hospital.hms.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentCodeIgnoreCase(String appointmentCode);

    List<Appointment> findByPatientOrderByAppointmentDateDescAppointmentTimeDesc(Patient patient);

    List<Appointment> findByDoctorOrderByAppointmentDateDescAppointmentTimeDesc(Doctor doctor);

    List<Appointment> findByAppointmentDateOrderByAppointmentTimeAsc(LocalDate date);

    List<Appointment> findByStatusOrderByAppointmentDateDescAppointmentTimeDesc(AppointmentStatus status);

    List<Appointment> findAllByOrderByAppointmentDateDescAppointmentTimeDesc();

    long countByStatus(AppointmentStatus status);

    long countByAppointmentDate(LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE " +
           "LOWER(a.appointmentCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.patient.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.patient.patientCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.doctor.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(a.chiefComplaint) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY a.appointmentDate DESC, a.appointmentTime DESC")
    List<Appointment> searchAppointments(@Param("query") String query);
}
