package com.hospital.hms;

import com.hospital.hms.model.Patient;
import com.hospital.hms.repository.PatientRepository;
import com.hospital.hms.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PatientServiceTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    void testRegisterAndFindPatient() {
        Patient p = new Patient("P-9999", "Test Patient", 40, "Female", "9998887776");
        p.setBloodGroup("A+");
        Patient saved = patientService.savePatient(p);

        assertNotNull(saved.getId());
        assertEquals("P-9999", saved.getPatientCode());

        Optional<Patient> found = patientService.findByPatientCode("P-9999");
        assertTrue(found.isPresent());
        assertEquals("Test Patient", found.get().getFullName());
    }

    @Test
    void testSearchPatients() {
        List<Patient> results = patientService.searchPatients("Rahul");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.getFullName().contains("Rahul")));

        List<Patient> codeResults = patientService.searchPatients("P-1001");
        assertFalse(codeResults.isEmpty());
    }

    @Test
    void testGenerateNextPatientCode() {
        String code = patientService.generateNextPatientCode();
        assertNotNull(code);
        assertTrue(code.startsWith("P-"));
        assertFalse(patientService.existsByPatientCode(code));
    }
}
