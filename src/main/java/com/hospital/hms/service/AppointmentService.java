package com.hospital.hms.service;

import com.hospital.hms.model.Appointment;
import com.hospital.hms.model.AppointmentStatus;
import com.hospital.hms.model.Doctor;
import com.hospital.hms.model.Patient;
import com.hospital.hms.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAllByOrderByAppointmentDateDescAppointmentTimeDesc();
    }

    public List<Appointment> searchAppointments(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllAppointments();
        }
        return appointmentRepository.searchAppointments(query.trim());
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        if (status == null) {
            return getAllAppointments();
        }
        return appointmentRepository.findByStatusOrderByAppointmentDateDescAppointmentTimeDesc(status);
    }

    public List<Appointment> getAppointmentsByPatient(Patient patient) {
        return appointmentRepository.findByPatientOrderByAppointmentDateDescAppointmentTimeDesc(patient);
    }

    public List<Appointment> getAppointmentsByDoctor(Doctor doctor) {
        return appointmentRepository.findByDoctorOrderByAppointmentDateDescAppointmentTimeDesc(doctor);
    }

    public List<Appointment> getAppointmentsForToday() {
        return appointmentRepository.findByAppointmentDateOrderByAppointmentTimeAsc(LocalDate.now());
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment saveAppointment(Appointment appointment) {
        if (appointment.getAppointmentCode() == null || appointment.getAppointmentCode().trim().isEmpty()) {
            appointment.setAppointmentCode(generateNextAppointmentCode());
        }
        return appointmentRepository.save(appointment);
    }

    public Appointment updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + id));
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    public long getTotalAppointmentsCount() {
        return appointmentRepository.count();
    }

    public long getScheduledCount() {
        return appointmentRepository.countByStatus(AppointmentStatus.SCHEDULED);
    }

    public long getTodayCount() {
        return appointmentRepository.countByAppointmentDate(LocalDate.now());
    }

    public String generateNextAppointmentCode() {
        long count = appointmentRepository.count() + 1001;
        String code = String.format("APT-%04d", count);
        while (appointmentRepository.findByAppointmentCodeIgnoreCase(code).isPresent()) {
            count++;
            code = String.format("APT-%04d", count);
        }
        return code;
    }
}
