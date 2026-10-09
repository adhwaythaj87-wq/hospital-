package com.hospital.hms;

import com.hospital.hms.model.Appointment;
import com.hospital.hms.model.AppointmentStatus;
import com.hospital.hms.model.Doctor;
import com.hospital.hms.model.Patient;
import com.hospital.hms.service.AppointmentService;
import com.hospital.hms.service.DoctorService;
import com.hospital.hms.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AppointmentServiceTest {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Test
    void testBookAndStatusTransition() {
        Patient patient = patientService.getAllPatients().get(0);
        Doctor doctor = doctorService.getAllDoctors().get(0);

        Appointment apt = new Appointment();
        apt.setPatient(patient);
        apt.setDoctor(doctor);
        apt.setAppointmentDate(LocalDate.now().plusDays(2));
        apt.setAppointmentTime(LocalTime.of(14, 30));
        apt.setChiefComplaint("Follow up consultation");
        apt.setStatus(AppointmentStatus.SCHEDULED);

        Appointment saved = appointmentService.saveAppointment(apt);
        assertNotNull(saved.getId());
        assertNotNull(saved.getAppointmentCode());
        assertEquals(AppointmentStatus.SCHEDULED, saved.getStatus());

        Appointment updated = appointmentService.updateStatus(saved.getId(), AppointmentStatus.COMPLETED);
        assertEquals(AppointmentStatus.COMPLETED, updated.getStatus());
    }

    @Test
    void testFindAppointmentsByPatient() {
        Patient patient = patientService.getAllPatients().get(0);
        List<Appointment> list = appointmentService.getAppointmentsByPatient(patient);
        assertNotNull(list);
    }
}
