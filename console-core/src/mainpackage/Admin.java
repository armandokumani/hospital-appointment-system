package mainpackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin extends Users {
    private List<Patient> patients;
    private List<Doctor> doctors;

    public Admin(String username, String password, String name, String surname, int age) {
        super(username, password, name, surname, age);
        this.patients = new ArrayList<>();
        this.doctors = new ArrayList<>();
    }
    // Getters for patients and doctors lists
    public List<Patient> getPatients() {
        return patients;
    }
    public List<Doctor> getDoctors() {
        return doctors;
    }

    // Patient management
    public void addPatient(Patient patient) {
        patients.add(patient);
        System.out.println("Patient added: " + patient.getName());
    }

    public void deletePatient(int index) {
        if (index >= 0 && index < patients.size()) {
            Patient removed = patients.remove(index);
            System.out.println("Removed patient: " + removed.getName());
        } else {
            System.out.println("Invalid patient index!");
        }
    }

    public void viewPatients() {
        System.out.println("\n=== PATIENT LIST ===");
        if (patients.isEmpty()) {
            System.out.println("No patients registered.");
        } else {
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i+1) + ". " + patients.get(i));
            }
        }
    }

    public void modifyPatient(int index, Scanner scanner) {
        if (index >= 0 && index < patients.size()) {
            Patient p = patients.get(index);
            System.out.println("\nModifying patient: " + p.getName());

            System.out.print("New username (" + p.getUsername() + "): ");
            String newUsername = scanner.nextLine();
            if (!newUsername.isEmpty()) p.setUsername(newUsername);

            System.out.print("New password: ");
            String newPassword = scanner.nextLine();
            if (!newPassword.isEmpty()) p.setPassword(newPassword);

            System.out.print("New name (" + p.getName() + "): ");
            String newName = scanner.nextLine();
            if (!newName.isEmpty()) p.setName(newName);

            System.out.print("New surname (" + p.getSurname() + "): ");
            String newSurname = scanner.nextLine();
            if (!newSurname.isEmpty()) p.setSurname(newSurname);

            System.out.print("New age (" + p.getAge() + "): ");
            String ageInput = scanner.nextLine();
            if (!ageInput.isEmpty()) p.setAge(Integer.parseInt(ageInput));

            System.out.println("Patient updated successfully!");
        } else {
            System.out.println("Invalid patient index!");
        }
    }

    // Doctor management
    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        System.out.println("Doctor added: " + doctor.getName());
    }

    public void viewDoctors() {
        System.out.println("\n=== DOCTOR LIST ===");
        if (doctors.isEmpty()) {
            System.out.println("No doctors registered.");
        } else {
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i+1) + ". " + doctors.get(i));
            }
        }
    }

    public void deleteDoctor(int index) {
        if (index >= 0 && index < doctors.size()) {
            Doctor removed = doctors.remove(index);
            System.out.println("Removed doctor: " + removed.getName());
        } else {
            System.out.println("Invalid doctor index!");
        }
    }
}