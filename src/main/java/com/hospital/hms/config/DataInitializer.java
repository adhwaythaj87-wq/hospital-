package com.hospital.hms.config;

import com.hospital.hms.model.*;
import com.hospital.hms.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final BillRepository billRepository;

    public DataInitializer(PatientRepository patientRepository,
                           DoctorRepository doctorRepository,
                           AppointmentRepository appointmentRepository,
                           PrescriptionRepository prescriptionRepository,
                           BillRepository billRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.billRepository = billRepository;
    }

    @Override
    public void run(String... args) {
        if (patientRepository.count() >= 30 && doctorRepository.count() >= 12) {
            // Already populated with large 100+ dataset
            return;
        }

        // Clean slate for fresh massive seeding
        if (billRepository.count() > 0) billRepository.deleteAll();
        if (prescriptionRepository.count() > 0) prescriptionRepository.deleteAll();
        if (appointmentRepository.count() > 0) appointmentRepository.deleteAll();
        if (patientRepository.count() > 0) patientRepository.deleteAll();
        if (doctorRepository.count() > 0) doctorRepository.deleteAll();

        Random random = new Random(42); // Deterministic seed for reproducible rich data

        // ==========================================
        // 1. Seed 15 Doctors across Major Specialties
        // ==========================================
        String[][] doctorData = {
            {"Dr. Rajesh Sharma", "Cardiology", "MD, DM (Cardiology)", "Room 102 (OPD A)", "700.00", "Mon - Fri (09:00 AM - 02:00 PM)", "9845011221"},
            {"Dr. Priya Nair", "Pediatrics", "MD (Pediatrics), DCH", "Room 105 (Child Care)", "500.00", "Mon - Sat (10:00 AM - 04:00 PM)", "9845011222"},
            {"Dr. Ananya Menon", "General Medicine", "MBBS, MD (Internal Medicine)", "Room 101 (OPD A)", "450.00", "All Days (08:30 AM - 01:30 PM)", "9845011223"},
            {"Dr. Vikram Rao", "Orthopedics", "MS (Ortho), DNB (Orthopedics)", "Room 108 (Bone Wing)", "600.00", "Mon, Wed, Fri (02:00 PM - 07:00 PM)", "9845011224"},
            {"Dr. Sarah Khan", "Dermatology", "MD (DVL), FAAD", "Room 110 (Skin Clinic)", "550.00", "Tue, Thu, Sat (11:00 AM - 05:00 PM)", "9845011225"},
            {"Dr. Arun Varma", "Neurology", "MD (Med), DM (Neurology)", "Room 201 (Neuro Wing)", "800.00", "Mon, Wed, Sat (10:00 AM - 03:00 PM)", "9845011226"},
            {"Dr. Kavitha Balan", "Gynecology", "MS (OBG), DGO, Fellowship IVF", "Room 204 (Women Care)", "600.00", "Mon - Fri (09:30 AM - 02:30 PM)", "9845011227"},
            {"Dr. Mathew Joseph", "ENT", "MS (ENT), DLO", "Room 112 (ENT Clinic)", "500.00", "Tue, Thu, Sat (09:00 AM - 01:00 PM)", "9845011228"},
            {"Dr. Divya Nambiar", "Ophthalmology", "MS (Ophthal), FICO (UK)", "Room 115 (Eye Center)", "500.00", "Mon - Sat (08:30 AM - 12:30 PM)", "9845011229"},
            {"Dr. Sanjay Kulkarni", "Pulmonology", "MD (Chest & Respiratory Med)", "Room 208 (Chest Clinic)", "650.00", "Mon, Wed, Fri (11:00 AM - 04:00 PM)", "9845011230"},
            {"Dr. Harish Pillai", "Gastroenterology", "MD, DM (Gastroenterology)", "Room 210 (Gastro Wing)", "750.00", "Tue, Thu, Sat (10:00 AM - 02:00 PM)", "9845011231"},
            {"Dr. Neha Kapoor", "Psychiatry", "MD (Psychiatry), DPM", "Room 215 (Mind Wellness)", "600.00", "Mon - Fri (03:00 PM - 07:00 PM)", "9845011232"},
            {"Dr. Suresh Reddy", "Nephrology", "MD, DM (Nephrology)", "Room 220 (Renal Care)", "800.00", "Mon, Wed, Fri (09:00 AM - 01:00 PM)", "9845011233"},
            {"Dr. Shalini Das", "Endocrinology", "MD, DM (Endocrinology)", "Room 118 (Diabetes Center)", "650.00", "Tue, Thu, Sat (02:00 PM - 06:00 PM)", "9845011234"},
            {"Dr. Manoj Varghese", "Oncology", "MD, DM (Medical Oncology)", "Room 301 (Cancer Care)", "900.00", "Mon - Fri (10:00 AM - 02:00 PM)", "9845011235"}
        };

        List<Doctor> doctors = new ArrayList<>();
        int docIndex = 101;
        for (String[] doc : doctorData) {
            String code = "D-" + docIndex++;
            Doctor d = new Doctor(code, doc[0], doc[1], "+91 " + doc[6], new BigDecimal(doc[4]));
            d.setQualification(doc[2]);
            d.setRoomNumber(doc[3]);
            d.setAvailableDays(doc[5]);
            d.setEmail(doc[0].toLowerCase().replace("dr. ", "").replace(" ", ".") + "@lifecare.org");
            doctors.add(doctorRepository.save(d));
        }

        // ==========================================
        // 2. Seed 40+ Patients across Demographics
        // ==========================================
        String[][] patientSeeds = {
            {"Rahul Varma", "34", "Male", "O+", "9876543210", "42 MG Road, Kochi", "Mild hypertension, no known allergies", "Pooja Varma (Wife)"},
            {"Meera Krishnan", "28", "Female", "B+", "9876543211", "15 Lake View Apt, Ernakulam", "Seasonal allergic rhinitis", "Krishnan Nair (Father)"},
            {"John Fernandes", "52", "Male", "A+", "9876543212", "88 Church Street, Fort Kochi", "Type 2 Diabetes, Knee osteoarthritis", "Mary Fernandes (Spouse)"},
            {"Sunita Patel", "45", "Female", "AB+", "9876543213", "12 Palm Grove, Kakkanad", "Hypothyroidism (on Thyroxine 50mcg)", "Ramesh Patel (Husband)"},
            {"Amit Kumar", "19", "Male", "O-", "9876543214", "7 University Road, Kalamassery", "Healthy athlete, no chronic conditions", "Suresh Kumar (Father)"},
            {"Deepa Thomas", "62", "Female", "B-", "9876543215", "24 Rose Gardens, Aluva", "Coronary artery disease, Post-stent (2023), Osteoporosis", "Thomas Joseph (Son)"},
            {"Rohan Sen", "8", "Male", "A-", "9876543216", "56 Riverdale Enclave, Tripunithura", "Recurrent tonsillitis & adenoid enlargement", "Anita Sen (Mother)"},
            {"Fatima Zahra", "29", "Female", "O+", "9876543217", "10 Marine Drive, Kochi", "Antenatal 24 weeks gestation, Mild anemia", "Ahmed Zahra (Husband)"},
            {"George Mathew", "41", "Male", "AB-", "9876543218", "31 Green Hill, Thevara", "Chronic migraine with visual aura", "Shiny Mathew (Wife)"},
            {"Lakshmi Pillai", "71", "Female", "B+", "9876543219", "9 Temple Road, Vyttila", "COPD (Grade 2), Hypertension, Cataract right eye", "Unnikrishnan (Son)"},
            {"Nikhil Joshi", "23", "Male", "A+", "9876543220", "18 Stadium Link Road, Kaloor", "Right ACL grade 1 strain", "Dinesh Joshi (Father)"},
            {"Anjali Gupta", "37", "Female", "O+", "9876543221", "5 Green Valley, Panampilly Nagar", "Chronic nummular eczema & allergic contact dermatitis", "Vivek Gupta (Husband)"},
            {"Vijay Narayanan", "58", "Male", "O+", "9876543222", "14 Heritage Lane, Mattancherry", "Essential tremor, Dyslipidemia", "Geetha Narayanan (Wife)"},
            {"Sneha Paul", "24", "Female", "A+", "9876543223", "302 Skyline Oasis, Palarivattom", "Polycystic Ovarian Syndrome (PCOS)", "Paul Varghese (Father)"},
            {"Abhishek Singh", "31", "Male", "B+", "9876543224", "77 Infopark Road, Kakkanad", "Chronic cervical spondylosis from desk work", "Preeti Singh (Wife)"},
            {"Kavita Shenoy", "49", "Female", "AB+", "9876543225", "21 Ferry Road, Willingdon Island", "Post-menopausal osteopenia, Vitamin D deficiency", "Mohan Shenoy (Husband)"},
            {"Joseph Kurian", "67", "Male", "O-", "9876543226", "103 Seaside Villa, Cherai", "Chronic Kidney Disease Stage 3, Hypertension", "Ann Kurian (Daughter)"},
            {"Nandini Iyer", "12", "Female", "B+", "9876543227", "8 Gandhi Nagar, Kadavanthra", "Childhood asthma triggered by dust", "Ramanathan Iyer (Father)"},
            {"Karthik Subramanian", "39", "Male", "A-", "9876543228", "92 IT Highway, Infopark Phase 2", "Gastroesophageal Reflux Disease (GERD)", "Swathi K (Wife)"},
            {"Amina Beevi", "54", "Female", "O+", "9876543229", "16 Mosque Lane, Aluva", "Uncontrolled Type 2 Diabetes, Diabetic Neuropathy", "Mohammad Aslam (Son)"},
            {"Devraj Hegde", "44", "Male", "B+", "9876543230", "44 Hill View, Thrikkakara", "Fatty Liver Disease Grade 1, Hyperuricemia", "Rashmi Hegde (Wife)"},
            {"Pooja Sundaram", "26", "Female", "A+", "9876543231", "67 College Road, Edappally", "Iron deficiency anemia, Chronic fatigue", "Sundaram (Father)"},
            {"Manoj Chacko", "63", "Male", "AB+", "9876543232", "11 Bethel Cottage, Angamaly", "Benign Prostatic Hyperplasia (BPH), Hypertension", "Susan Chacko (Wife)"},
            {"Ritu Agarwal", "33", "Female", "O+", "9876543233", "201 Emerald Heights, Ravipuram", "Anxiety disorder with panic attacks", "Gaurav Agarwal (Husband)"},
            {"Gopalakrishnan N", "76", "Male", "B+", "9876543234", "3 Old Bazaar, Perumbavoor", "Parkinson's disease early stage, Chronic constipation", "Sreejith G (Son)"},
            {"Divya Menon", "30", "Female", "A-", "9876543235", "19 Golf Links, Bolgatty", "Thyroid nodule under evaluation, Euthyroid", "Siddharth (Husband)"},
            {"Hariprasad Rao", "50", "Male", "O+", "9876543236", "55 Port Road, Kochi", "Chronic lower back lumbago", "Sujatha Rao (Wife)"},
            {"Saira Banu", "22", "Female", "B-", "9876543237", "8 West Canal Road, Maradu", "Acne vulgaris with facial scarring", "Farooq (Father)"},
            {"Tarun Balakrishnan", "16", "Male", "AB+", "9876543238", "102 St. Mary's Colony, Kacheripady", "Acute allergic conjunctivitis", "Balan K (Father)"},
            {"Mary Varkey", "69", "Female", "O+", "9876543239", "7 Nazareth House, Fort Kochi", "Rheumatoid Arthritis on Methotrexate", "Varkey Joseph (Husband)"},
            {"Siddharth Roy", "27", "Male", "A+", "9876543240", "84 SmartCity Boulevard, Kakkanad", "Tension headache and insomnia", "Indrani Roy (Mother)"},
            {"Leela Namboodiri", "82", "Female", "B+", "9876543241", "2 Mana Road, Piravom", "General geriatric frailty, Mild dementia", "Vasudevan (Son)"},
            {"Basil Eldho", "35", "Male", "O-", "9876543242", "40 Riverbank, Kothamangalam", "Chronic sinusitis with nasal polyps", "Mariya Eldho (Wife)"},
            {"Zoya Farhan", "4", "Female", "A+", "9876543243", "14 Star Homes, Vazhakkala", "Viral croup with stridor and dry cough", "Farhan Ahmed (Father)"},
            {"Pradeep Nambiar", "48", "Male", "B+", "9876543244", "39 Silver Beach, Njarackal", "Chronic viral hepatitis B carrier (inactive)", "Remya Nambiar (Wife)"},
            {"Shobha Kumari", "57", "Female", "AB-", "9876543245", "91 Valley View, Kaloor", "Fibromyalgia, Chronic widespread musculoskeletal pain", "Rajendran (Husband)"},
            {"Arjun Somnath", "20", "Male", "O+", "9876543246", "3 Kusumanagiri, Kakkanad", "Acute gastroenteritis post hostel food", "Somnath (Father)"},
            {"Grace Stanley", "66", "Female", "A-", "9876543247", "50 Queen's Way, Marine Drive", "Hypertensive nephropathy, Peripheral neuropathy", "Stanley (Husband)"},
            {"Roshan Jacob", "38", "Male", "B+", "9876543248", "12 Cedar Park, Kadavanthra", "Recurrent kidney stones (Urolithiasis)", "Tessa Jacob (Wife)"},
            {"Geethanjali S", "42", "Female", "O+", "9876543249", "88 Palm Meadows, Thoppumpady", "Perimenopausal hot flashes & insomnia", "Subramanian (Husband)"}
        };

        List<Patient> patients = new ArrayList<>();
        int patIndex = 1001;
        for (String[] pat : patientSeeds) {
            String code = "P-" + patIndex++;
            Patient p = new Patient(code, pat[0], Integer.parseInt(pat[1]), pat[2], pat[4]);
            p.setBloodGroup(pat[3]);
            p.setAddress(pat[5]);
            p.setMedicalHistory(pat[6]);
            p.setEmergencyContact(pat[7] + " - " + pat[4]);
            p.setEmail(pat[0].toLowerCase().replace(" ", ".") + "@example.com");
            patients.add(patientRepository.save(p));
        }

        // ==========================================
        // 3. Seed 50+ Appointments (Past, Today, Future)
        // ==========================================
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = new ArrayList<>();
        int aptCode = 1001;

        String[] visitReasons = {
            "Routine health checkup and blood pressure monitoring",
            "Acute viral fever with body chills and fatigue",
            "Severe throbbing headache and photophobia",
            "Knee joint pain during stair climbing",
            "Pediatric wellness checkup and vaccination advice",
            "Skin rash, redness and continuous itching",
            "Persistent dry cough and shortness of breath",
            "Abdominal cramps, acidity, and postprandial bloating",
            "Follow-up for glycemic optimization and HbA1c review",
            "Cardiac review post-medication titration",
            "Ear pain and muffled hearing after swimming",
            "Eye strain, blurred distance vision and dry eyes",
            "Routine antenatal scan and supplement review",
            "Sleep disturbance and acute anxiety consultation",
            "Lower back stiffness after heavy lifting"
        };

        LocalTime[] timeSlots = {
            LocalTime.of(9, 0), LocalTime.of(9, 30), LocalTime.of(10, 0), LocalTime.of(10, 30),
            LocalTime.of(11, 0), LocalTime.of(11, 30), LocalTime.of(12, 0), LocalTime.of(14, 0),
            LocalTime.of(14, 30), LocalTime.of(15, 0), LocalTime.of(15, 30), LocalTime.of(16, 0),
            LocalTime.of(16, 30), LocalTime.of(17, 0)
        };

        for (int i = 0; i < 52; i++) {
            Patient pt = patients.get(i % patients.size());
            Doctor doc = doctors.get(i % doctors.size());
            int dayOffset = (i % 15) - 7; // range from 7 days ago to 7 days in future
            LocalDate aptDate = today.plusDays(dayOffset);
            LocalTime aptTime = timeSlots[i % timeSlots.length];
            String reason = visitReasons[i % visitReasons.length];

            Appointment apt = new Appointment("APT-" + (aptCode++), pt, doc, aptDate, aptTime, reason);

            if (dayOffset < 0) {
                apt.setStatus(i % 10 == 0 ? AppointmentStatus.CANCELLED : AppointmentStatus.COMPLETED);
                apt.setNotes("Consultation completed. Advice recorded in patient medical file.");
            } else if (dayOffset == 0) {
                apt.setStatus(i % 2 == 0 ? AppointmentStatus.COMPLETED : AppointmentStatus.SCHEDULED);
                apt.setNotes("Today's outpatient OPD slot.");
            } else {
                apt.setStatus(AppointmentStatus.SCHEDULED);
                apt.setNotes("Advance booking confirmed via reception portal.");
            }

            appointments.add(appointmentRepository.save(apt));
        }

        // ==========================================
        // 4. Seed 35+ Detailed Prescriptions (Rx)
        // ==========================================
        String[][] rxDiagnoses = {
            {"Acute Upper Respiratory Tract Infection", "Amoxicillin + Clavulanate 625mg", "1 Tab", "1-0-1", "5 Days", "After meals", "Montelukast 10mg", "1 Tab", "0-0-1", "7 Days", "At night", "Paracetamol 650mg", "1 Tab", "SOS", "3 Days", "If temp > 100F", "Steam inhalation; warm saline gargle twice daily."},
            {"Lumbar Spondylosis & Radiculopathy", "Aceclofenac + Paracetamol 100/325mg", "1 Tab", "1-0-1", "5 Days", "After food", "Thiocolchicoside 4mg", "1 Cap", "1-0-1", "5 Days", "Muscle relaxant", "Pantoprazole 40mg", "1 Tab", "1-0-0", "5 Days", "Before breakfast", "Avoid heavy lifting; lumbar support belt during travel."},
            {"Acute Bacterial Gastroenteritis", "Ofloxacin + Ornidazole 200/500mg", "1 Tab", "1-0-1", "3 Days", "After meals", "Bacillus Clausii Spores", "1 Mini Bottle", "1-0-1", "5 Days", "Oral suspension", "ORS Hydration Sachet", "1 Sachet in 1L", "Daily", "3 Days", "Continuous sip", "Bland diet (curd rice/khichdi); 3L fluid intake daily."},
            {"Essential Hypertension & Hyperlipidemia", "Telmisartan 40mg", "1 Tab", "1-0-0", "30 Days", "Morning", "Atorvastatin 20mg", "1 Tab", "0-0-1", "30 Days", "At bedtime", "Aspirin 75mg", "1 Tab", "0-1-0", "30 Days", "After lunch", "Low sodium diet (<5g/day); brisk 30 min walking; recheck lipid panel in 6 weeks."},
            {"Type 2 Diabetes Mellitus - Uncontrolled", "Metformin 1000mg SR", "1 Tab", "1-0-1", "30 Days", "With major meals", "Glimepiride 2mg", "1 Tab", "1-0-0", "30 Days", "Before breakfast", "Vildagliptin 50mg", "1 Tab", "1-0-1", "30 Days", "After meals", "Strict carbohydrate restriction; monitor fasting blood glucose thrice weekly."},
            {"Acute Migraine with Visual Aura", "Naproxen Sodium 500mg", "1 Tab", "SOS", "5 Days", "At aura onset", "Domperidone 10mg", "1 Tab", "SOS", "5 Days", "With painkiller", "Flunarizine 5mg", "1 Tab", "0-0-1", "30 Days", "At night", "Maintain migraine trigger diary; ensure 8 hours restful sleep."},
            {"COPD with Bronchospasm", "Formoterol + Budesonide Inhaler 200mcg", "2 Puffs", "1-0-1", "30 Days", "Inhalation", "N-Acetylcysteine 600mg Effervescent", "1 Tab in water", "1-0-0", "10 Days", "Morning", "Levocetirizine 5mg", "1 Tab", "0-0-1", "10 Days", "Night", "Rinse mouth thoroughly after inhaler use; avoid dust and passive smoke."},
            {"Nummular Eczema & Pruritus", "Mometasone Furoate 0.1% Cream", "Apply thin layer", "1-0-1", "7 Days", "External use", "Cetirizine 10mg", "1 Tab", "0-0-1", "10 Days", "Night", "Ceramide Moisturizer", "Liberal application", "2-3 times", "30 Days", "Whole body", "Use syndet soap-free bars; avoid wool and synthetic garments."},
            {"Allergic Rhinitis with Sinusitis", "Fluticasone Furoate Nasal Spray", "2 Sprays each nostril", "1-0-0", "15 Days", "Morning", "Fexofenadine 120mg", "1 Tab", "1-0-0", "10 Days", "After breakfast", "Xylometazoline 0.1% Drops", "2 Drops", "SOS", "3 Days", "Max 3 days", "Do not overuse decongestant drops; steam inhalation twice daily."},
            {"Geriatric Osteoarthritis Knee Grade 2", "Diacerein + Glucosamine 50/750mg", "1 Tab", "1-0-1", "30 Days", "After meals", "Calcium Citrate Malate + Vit D3", "1 Tab", "0-1-0", "30 Days", "After lunch", "Diclofenac Sodium Gel 1%", "Gentle massage", "1-0-1", "15 Days", "Topical application", "Quadriceps strengthening physiotherapy exercises; avoid deep squats."}
        };

        List<Prescription> prescriptions = new ArrayList<>();
        int rxCode = 1001;

        for (int i = 0; i < 35; i++) {
            Appointment matchedApt = appointments.get(i % appointments.size());
            Patient pt = matchedApt.getPatient();
            Doctor doc = matchedApt.getDoctor();
            LocalDate rxDate = matchedApt.getAppointmentDate();

            String[] diagTemplate = rxDiagnoses[i % rxDiagnoses.length];

            Prescription rx = new Prescription("RX-" + (rxCode++), pt, doc, rxDate, diagTemplate[0]);
            rx.setAppointment(matchedApt);
            rx.setAdvice(diagTemplate[15]);

            // Add Medicine 1
            rx.addItem(new PrescriptionItem(diagTemplate[1], diagTemplate[2], diagTemplate[3], diagTemplate[4], diagTemplate[5]));
            // Add Medicine 2
            rx.addItem(new PrescriptionItem(diagTemplate[6], diagTemplate[7], diagTemplate[8], diagTemplate[9], diagTemplate[10]));
            // Add Medicine 3
            rx.addItem(new PrescriptionItem(diagTemplate[11], diagTemplate[12], diagTemplate[13], diagTemplate[14], diagTemplate[5]));

            prescriptions.add(prescriptionRepository.save(rx));
        }

        // ==========================================
        // 5. Seed 45+ Itemized Bills & Invoices
        // ==========================================
        PaymentMethod[] paymentMethods = PaymentMethod.values();
        int billCode = 1001;

        for (int i = 0; i < 45; i++) {
            Appointment apt = appointments.get(i % appointments.size());
            Patient pt = apt.getPatient();
            Doctor doc = apt.getDoctor();
            LocalDate bDate = apt.getAppointmentDate();

            BigDecimal consultFee = doc.getConsultationFee();
            BigDecimal medCharge = new BigDecimal(150 + (random.nextInt(12) * 50) + ".00");
            BigDecimal labCharge = (i % 3 == 0) ? new BigDecimal(300 + (random.nextInt(8) * 100) + ".00") : BigDecimal.ZERO;
            BigDecimal discount = (i % 5 == 0) ? new BigDecimal("100.00") : BigDecimal.ZERO;
            BigDecimal tax = new BigDecimal(15 + (random.nextInt(5) * 10) + ".00");

            Bill bill = new Bill("INV-" + (billCode++), pt, doc, bDate, consultFee, medCharge);
            bill.setAppointment(apt);
            if (i < prescriptions.size()) {
                bill.setPrescription(prescriptions.get(i));
            }
            bill.setLabCharges(labCharge);
            bill.setDiscount(discount);
            bill.setTaxAmount(tax);
            bill.calculateTotal();

            BigDecimal total = bill.getTotalAmount();

            if (i % 6 == 0) {
                // Pending Bill
                bill.setPaymentStatus(PaymentStatus.PENDING);
                bill.setPaidAmount(BigDecimal.ZERO);
                bill.setPaymentMethod(PaymentMethod.CASH);
                bill.setNotes("Pending payment awaiting counter settlement.");
            } else if (i % 7 == 0) {
                // Partially Paid Bill
                bill.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
                BigDecimal advance = total.multiply(new BigDecimal("0.5")).setScale(2, java.math.RoundingMode.HALF_UP);
                bill.setPaidAmount(advance);
                bill.setPaymentMethod(PaymentMethod.UPI);
                bill.setPaidAt(LocalDateTime.of(bDate, LocalTime.of(12, 30)));
                bill.setNotes("Advance deposit of ₹" + advance + " received; balance pending.");
            } else {
                // Fully Paid Bill
                bill.setPaymentStatus(PaymentStatus.PAID);
                bill.setPaidAmount(total);
                bill.setPaymentMethod(paymentMethods[i % paymentMethods.length]);
                bill.setPaidAt(LocalDateTime.of(bDate, LocalTime.of(11, 45)));
                bill.setNotes("Settled in full via " + bill.getPaymentMethod().name() + " at billing desk.");
            }

            billRepository.save(bill);
        }

        long totalRecords = patientRepository.count() + doctorRepository.count() +
                            appointmentRepository.count() + prescriptionRepository.count() +
                            billRepository.count();

        System.out.println("===================================================================");
        System.out.println(">>> LifeCare HMS 100+ Dataset Initialized Successfully!");
        System.out.println("    - Doctors:       " + doctorRepository.count());
        System.out.println("    - Patients:      " + patientRepository.count());
        System.out.println("    - Appointments:  " + appointmentRepository.count());
        System.out.println("    - Prescriptions: " + prescriptionRepository.count());
        System.out.println("    - Bills/Invoices:" + billRepository.count());
        System.out.println("    - TOTAL RECORDS: " + totalRecords + " records");
        System.out.println("===================================================================");
    }
}
