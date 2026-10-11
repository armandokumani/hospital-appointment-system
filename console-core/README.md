# Console Core Module - Hospital Management Domain

This module represents the foundational Object-Oriented Programming (OOP) core of the Hospital Management System. It was originally engineered as a standalone domain application before evolving into the Jakarta EE web architecture.

## Architecture & OOP Principles
- **Inheritance & Polymorphism**: Abstract/base entity `Users` extended by `Admin`, `Doctor`, and `Patient`.
- **Encapsulation**: Private state management, validation methods, and credential handling.
- **File Persistence**: Manages and parses patient record sets via `patient_data.txt`.
- **Domain Entities**:
  - `Admin.java`: Administrative user operations.
  - `Doctor.java`: Doctor profile, specialties, and schedule.
  - `Patient.java`: Medical record entity with identification (AMKA).
  - `Appointment.java`: Booking coordination between patient and physician.
  - `CreateUsers.java`: User generation, verification, and interactive console flow.

## Running the Console Core
Compile and run using standard Java:
```bash
javac -d bin src/mainpackage/*.java
java -cp bin mainpackage.CreateUsers
```
