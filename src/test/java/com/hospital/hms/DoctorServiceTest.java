package com.hospital.hms;

import com.hospital.hms.model.Doctor;
import com.hospital.hms.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class DoctorServiceTest {

    @Autowired
    private DoctorService doctorService;

    @Test
    void testAddAndFindDoctor() {
        Doctor doc = new Doctor("D-999", "Dr. Test Specialist", "Neurology", "+91 91234 56789", new BigDecimal("650.00"));
        Doctor saved = doctorService.saveDoctor(doc);

        assertNotNull(saved.getId());
        assertEquals("D-999", saved.getDoctorCode());

        Optional<Doctor> found = doctorService.findByDoctorCode("D-999");
        assertTrue(found.isPresent());
        assertEquals("Dr. Test Specialist", found.get().getFullName());
    }

    @Test
    void testSearchDoctors() {
        List<Doctor> cardioDocs = doctorService.searchDoctors("Cardiology");
        assertFalse(cardioDocs.isEmpty());
        assertTrue(cardioDocs.stream().anyMatch(d -> d.getSpecialization().equalsIgnoreCase("Cardiology")));

        List<Doctor> phoneDocs = doctorService.searchDoctors("98450");
        assertFalse(phoneDocs.isEmpty());
    }

    @Test
    void testGetDoctorsBySpecialization() {
        List<Doctor> cardioList = doctorService.getDoctorsBySpecialization("Cardiology");
        assertFalse(cardioList.isEmpty());
        assertEquals("Cardiology", cardioList.get(0).getSpecialization());

        List<Doctor> allDocs = doctorService.getDoctorsBySpecialization(null);
        assertEquals(doctorService.getAllDoctors().size(), allDocs.size());
    }

    @Test
    void testGetAllSpecializations() {
        List<String> specs = doctorService.getAllSpecializations();
        assertNotNull(specs);
        assertFalse(specs.isEmpty());
        assertTrue(specs.contains("Cardiology"));
        assertTrue(specs.contains("Pediatrics"));
    }

    @Test
    void testGenerateNextDoctorCode() {
        String code = doctorService.generateNextDoctorCode();
        assertNotNull(code);
        assertTrue(code.startsWith("D-"));
        assertFalse(doctorService.existsByDoctorCode(code));
    }
}
