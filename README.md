# LifeCare Hospital Management System (HMS)
**College Java Web Application Project**

A modern, full-stack Hospital Management System built with **Java 17**, **Spring Boot 3**, **Thymeleaf**, and **Spring Data JPA**.

---

## 📌 Project Overview
This project upgrades the original Java console prototype into a complete, database-backed web application providing core healthcare workflows:
1. **Patient Management**: Register new patients with auto-generated IDs (`P-1001`), update demographic and contact information, and search by ID, name, or phone.
2. **Doctor Directory**: Manage medical specialists, departments, room allocations, consultation fees, and OPD schedules.
3. **Appointment Scheduling**: Book doctor appointments, manage consultation slots, and track statuses (`Scheduled`, `Completed`, `Cancelled`).
4. **Prescription Management (Rx)**: Create structured clinical prescriptions with dynamic medicine rows (medicine name, dosage, frequency, duration, instructions), validation rejecting unlinked patient/doctor IDs, and printable prescription slips.
5. **Hospital Billing & Accounts**: Calculate itemized invoices with fixed consultation fees, medicine charges, lab fees, taxes, discounts, and printable tax receipts with instant payment recording.
6. **Patient 360 Medical Records**: Unified digital health history displaying a patient's complete profile, appointments, prescriptions, and billing statements in one screen.

---

## 🛠️ Technology Stack
- **Language**: Java SE 17 (LTS)
- **Framework**: Spring Boot 3.2.4 (Spring MVC, Spring Data JPA, Spring Validation)
- **Template Engine**: Thymeleaf (Server-Side HTML5 UI)
- **Persistence / Database**:
  - **Embedded H2 File Persistence** (`./data/hospitaldb`) — Works out-of-the-box with **zero external installation** and persists records across restarts.
  - **H2 Web Console**: Accessible at `http://localhost:8080/h2-console`
  - **MySQL Database Support**: Pre-configured in `application.properties` (simply uncomment the MySQL configuration block).
- **Frontend / Styling**: Custom clinical CSS and JavaScript (clean responsive layout, no external CDN dependencies required for 100% offline support, printable `@media print` receipts).
- **Build Tool**: Apache Maven Wrapper (`mvnw` / `mvnw.cmd`)

---

## 🚀 How to Run the Project

### Option 1: Using Maven Wrapper (Recommended)
Open a terminal in this project directory and run:

**On Windows:**
```powershell
.\mvnw.cmd spring-boot:run
```

**On Linux / macOS:**
```bash
./mvnw spring-boot:run
```

### Option 2: Using the One-Click Run Script
Double-click `run.bat` (or execute `.\run.bat` in PowerShell / Command Prompt).

---

## 🌐 Accessing the Application
Once the application starts, open your browser and navigate to:
- **Web Application Portal**: [http://localhost:8080](http://localhost:8080)
- **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - *JDBC URL*: `jdbc:h2:file:./data/hospitaldb`
  - *User Name*: `sa`
  - *Password*: `(leave blank)`

---

## 🧪 Running the Test Suite
To execute the automated JUnit 5 and Spring Boot MockMvc test suite:
```powershell
.\mvnw.cmd test
```
All 41 unit and integration test cases covering services, models, business rules, and controllers are verified and pass with 100% success rate.

---

## 📂 Project Structure
```
hosp/
├── pom.xml                               # Maven Project Configuration
├── mvnw & mvnw.cmd                       # Maven Wrapper Executables
├── run.bat                               # Convenience Windows Startup Script
├── data/                                 # Persistent H2 Database Storage
└── src/
    ├── main/
    │   ├── java/com/hospital/hms/
    │   │   ├── HospitalManagementApplication.java
    │   │   ├── config/                   # Seed Data Initializer
    │   │   ├── model/                    # JPA Entities (Patient, Doctor, Appointment, Prescription, Bill)
    │   │   ├── repository/               # Spring Data JPA Repositories
    │   │   ├── service/                  # Business Logic Layer
    │   │   ├── controller/               # Spring MVC Web Controllers
    │   │   └── dto/                      # Form Data Transfer Objects
    │   └── resources/
    │       ├── application.properties    # H2 & MySQL Settings
    │       ├── static/css/style.css      # Clinical UI Theme & Print Styles
    │       ├── static/js/main.js         # Dynamic Medicine Rows & Live Calculator
    │       └── templates/                # Thymeleaf HTML5 Views
    └── test/                             # Automated JUnit 5 Test Suite
```
