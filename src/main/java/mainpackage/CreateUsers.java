package mainpackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class CreateUsers {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //Admin admin = initializeSystem();

        System.out.println("~HEALTHCARE MANAGEMENT SYSTEM~");
//        System.out.println("1. Admin Mode");
//        System.out.println("2. Patient Mode");
//        System.out.println("3. Doctor Mode");
//        System.out.print("Select mode: ");
//
//        String mode = scanner.nextLine();
//
//        switch (mode) {
//            case "1":
//                adminMenu(admin, scanner);
//                break;
//            case "2":
//                patientMenu(admin, scanner);
//                break;
//            case "3":
//                doctorMenu(admin, scanner);
//                break;
//            default:
//                System.out.println("Invalid selection");
//        }
//        scanner.close();
    }

//    private static Admin initializeSystem() {
//        Admin admin = new Admin("admin", "admin123", "System", "Admin", 40, "ADMIN");
//
//        // Add sample doctors
//        Doctor doc1 = new Doctor("doc1", "pass1", "John", "Doe", 45, "Cardiology", "DOCTOR");
//        Doctor doc2 = new Doctor("doc2", "pass2", "Jane", "Smith", 38, "Pediatrics", "DOCTOR");
//        admin.addDoctor(doc1);
//        admin.addDoctor(doc2);
//
//        // Add sample patients
//        Patient pat1 = new Patient("pat1", "pass1", "Mike", "Johnson", 30, 123456789, "PATIENT");
//        Patient pat2 = new Patient("pat2", "pass2", "Sarah", "Williams", 25, 987654321, "PATIENT");
//        admin.addPatient(pat1);
//        admin.addPatient(pat2);
//
//        // Add sample appointments
//        doc1.addAvailability(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0));
//        doc1.addAvailability(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
//        doc2.addAvailability(LocalDateTime.now().plusDays(2).withHour(11).withMinute(0));
//
//        return admin;
//    }

    private static void adminMenu(Admin admin, Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Manage Patients");
            System.out.println("2. Manage Doctors");
            System.out.println("3. View System Stats");
            System.out.println("4. Exit");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    managePatients(admin, scanner);
                    break;
                case "2":
                    manageDoctors(admin, scanner);
                    break;
                case "3":
                    System.out.println("\n=== SYSTEM STATISTICS ===");
                    System.out.println("Total users: " + Users.getUserCounter());
                    System.out.println("Doctors: " + admin.getDoctors().size());
                    System.out.println("Patients: " + admin.getPatients().size());
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static void managePatients(Admin admin, Scanner scanner) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n=== PATIENT MANAGEMENT ===");
            System.out.println("1. Add Patient (Manual Entry)");
            System.out.println("2. Add Patient (From File)");
            System.out.println("3. View Patients");
            System.out.println("4. Modify Patient");
            System.out.println("5. Delete Patient");
            System.out.println("6. Back");
            System.out.print("Select option: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    addPatient(admin, scanner);
                    break;
                case "2":
                    addPatientFromFile(admin, scanner);
                    break;
                case "3":
                    admin.viewPatients();
                    break;
                case "4":
                    admin.viewPatients();
                    if (!admin.getPatients().isEmpty()) {
                        System.out.print("Enter patient number to modify: ");
                        int index = Integer.parseInt(scanner.nextLine()) - 1;
                        admin.modifyPatient(index, scanner);
                    }
                    break;
                case "5":
                    admin.viewPatients();
                    if (!admin.getPatients().isEmpty()) {
                        System.out.print("Enter patient number to delete: ");
                        int index = Integer.parseInt(scanner.nextLine()) - 1;
                        admin.deletePatient(index);
                    }
                    break;
                case "6":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static void addPatientFromFile(Admin admin, Scanner scanner) {
        System.out.println("\n=== ADD PATIENT FROM FILE ===");
        System.out.print("Enter filename (with the full file path:  " +
                "C:\\Users\\panos\\IdeaProjects\\JavaBasics\\src\\mainpackage\\patient_data.txt): ");
        String filename = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] attributes = line.split("\\s+"); // Split by any whitespace

                    if (attributes.length != 6) {
                        System.err.println("Skipping invalid line - must contain exactly 6 attributes");
                        continue;
                    }

                    String username = attributes[0];
                    String password = attributes[1];
                    String name = attributes[2];
                    String surname = attributes[3];
                    int age = Integer.parseInt(attributes[4]);
                    long AMKA = Long.parseLong(attributes[5]); // Changed to long to match Patient constructor

                    // Check if patient already exists
                    boolean exists = admin.getPatients().stream()
                            .anyMatch(p -> p.getUsername().equals(username) ||
                                    p.getAMKA() == AMKA);

                    if (exists) {
                        System.out.println("Patient with username " + username +
                                " or AMKA " + AMKA + " already exists. Skipping.");
                        continue;
                    }

                    //Patient patient = new Patient(username, password, name, surname, age, AMKA, "PATIENT");
                    //admin.addPatient(patient);
                    //patient.registrationPatient();
                    //System.out.println("Successfully registered patient: " + patient.getName());

                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid line - age/AMKA must be numbers: " + line);
                } catch (IllegalArgumentException e) {
                    System.err.println("Skipping invalid line: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void addPatient(Admin admin, Scanner scanner) {
        System.out.println("\n=== ADD NEW PATIENT ===");

        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            if (username.isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (password.isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            System.out.print("Name: ");
            String name = scanner.nextLine();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }

            System.out.print("Surname: ");
            String surname = scanner.nextLine();
            if (surname.isEmpty()) {
                throw new IllegalArgumentException("Surname cannot be empty");
            }

            System.out.print("Age: ");
            int age;
            try {
                age = Integer.parseInt(scanner.nextLine());
                if (age <= 0 || age > 120) {
                    throw new IllegalArgumentException("Age must be between 1 and 120");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Age must be a valid number");
            }

            System.out.print("AMKA (9 digits): ");
            long amka;
            try {
                amka = Long.parseLong(scanner.nextLine());
                if (String.valueOf(amka).length() != 9) {
                    throw new IllegalArgumentException("AMKA must be exactly 9 digits");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("AMKA must be a valid number");
            }

            boolean amkaExists = admin.getPatients().stream()
                    .anyMatch(p -> p.getAMKA() == amka);
            if (amkaExists) {
                throw new IllegalArgumentException("Patient with AMKA " + amka + " already exists");
            }

            boolean usernameExists = admin.getPatients().stream()
                    .anyMatch(p -> p.getUsername().equals(username));
            if (usernameExists) {
                throw new IllegalArgumentException("Username " + username + " already exists");
            }

            //Patient patient = new Patient(username, password, name, surname, age, amka, "PATIENT");
            //admin.addPatient(patient);
           // patient.registrationPatient();
            System.out.println("Patient added successfully!");

        } catch (IllegalArgumentException e) {
            System.err.println("Error creating patient: " + e.getMessage());
            System.out.println("Please try again with valid data");
        }
    }

    private static void manageDoctors(Admin admin, Scanner scanner) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n=== DOCTOR MANAGEMENT ===");
            System.out.println("1. Add Doctor");
            System.out.println("2. View Doctors");
            System.out.println("3. Delete Doctor");
            System.out.println("4. Back");
            System.out.print("Select option: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    addDoctor(admin, scanner);
                    break;
                case "2":
                    admin.viewDoctors();
                    break;
                case "3":
                    admin.viewDoctors();
                    if (!admin.getDoctors().isEmpty()) {
                        System.out.print("Enter doctor number to delete: ");
                        int index = Integer.parseInt(scanner.nextLine()) - 1;
                        admin.deleteDoctor(index);
                    }
                    break;
                case "4":
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static void addDoctor(Admin admin, Scanner scanner) {
        System.out.println("\n=== ADD NEW DOCTOR ===");

        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            if (username.isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty");
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();
            if (password.isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty");
            }

            System.out.print("Name: ");
            String name = scanner.nextLine();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }

            System.out.print("Surname: ");
            String surname = scanner.nextLine();
            if (surname.isEmpty()) {
                throw new IllegalArgumentException("Surname cannot be empty");
            }

            System.out.print("Age: ");
            int age;
            try {
                age = Integer.parseInt(scanner.nextLine());
                if (age <= 25 || age > 80) {
                    throw new IllegalArgumentException("Doctor age must be between 26 and 80");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Age must be a valid number");
            }

            System.out.print("Specialty: ");
            String specialty = scanner.nextLine();
            if (specialty.isEmpty()) {
                throw new IllegalArgumentException("Specialty cannot be empty");
            }

            boolean usernameExists = admin.getDoctors().stream()
                    .anyMatch(d -> d.getUsername().equals(username));
            if (usernameExists) {
                throw new IllegalArgumentException("Username " + username + " already exists");
            }

            //Doctor doctor = new Doctor(username, password, name, surname, age, specialty, "DOCTOR");
            //admin.addDoctor(doctor);
            System.out.println("Doctor added successfully!");

        } catch (IllegalArgumentException e) {
            System.err.println("Error creating doctor: " + e.getMessage());
            System.out.println("Please try again with valid data");
        }
    }

    private static void patientMenu(Admin admin, Scanner scanner) {
        System.out.println("\n=== PATIENT LOGIN ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Patient patient = null;
        for (Patient p : admin.getPatients()) {
            if (p.getUsername().equals(username) && p.getPassword().equals(password)) {
                patient = p;
                break;
            }
        }

        if (patient == null) {
            System.out.println("Invalid credentials or patient not found");
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=== PATIENT MENU ===");
            System.out.println("1. View Upcoming Appointments");
            System.out.println("2. View Appointment History");
            System.out.println("3. Book Appointment");
            System.out.println("4. Logout");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    patient.viewUpcomingAppointments();
                    break;
                case "2":
                    patient.viewAppointmentHistory();
                    break;
                case "3":
                    bookAppointmentMenu(admin, patient, scanner);
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static void bookAppointmentMenu(Admin admin, Patient patient, Scanner scanner) {
        System.out.println("\n=== BOOK APPOINTMENT ===");
        admin.viewDoctors();
        System.out.print("Select doctor number: ");
        int docIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (docIndex < 0 || docIndex >= admin.getDoctors().size()) {
            System.out.println("Invalid doctor selection");
            return;
        }

        Doctor selectedDoctor = admin.getDoctors().get(docIndex);
        List<Appointment> available = selectedDoctor.getAvailableAppointments();

        if (available.isEmpty()) {
            System.out.println("No available appointments with this doctor");
            return;
        }

        System.out.println("\nAvailable appointments:");
        for (int i = 0; i < available.size(); i++) {
            System.out.println((i + 1) + ". " + available.get(i).getDateTime());
        }

        System.out.print("Select appointment number: ");
        int apptIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (apptIndex >= 0 && apptIndex < available.size()) {
            patient.bookAppointment(available.get(apptIndex));
        } else {
            System.out.println("Invalid appointment selection");
        }
    }

    private static void doctorMenu(Admin admin, Scanner scanner) {
        System.out.println("\n=== DOCTOR LOGIN ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Doctor doctor = null;
        for (Doctor d : admin.getDoctors()) {
            if (d.getUsername().equals(username) && d.getPassword().equals(password)) {
                doctor = d;
                break;
            }
        }

        if (doctor == null) {
            System.out.println("Invalid credentials or doctor not found");
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=== DR. " + doctor.getSurname().toUpperCase() + "'S MENU ===");
            System.out.println("1. View Schedule");
            System.out.println("2. Add Availability");
            System.out.println("3. Cancel Appointment");
            System.out.println("4. Logout");
            System.out.print("Select option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    doctor.viewSchedule();
                    break;
                case "2":
                    addAvailabilityMenu(doctor, scanner);
                    break;
                case "3":
                    cancelAppointmentMenu(doctor, scanner);
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static void addAvailabilityMenu(Doctor doctor, Scanner scanner) {
        System.out.println("\n=== ADD AVAILABILITY ===");
        System.out.print("Enter date and time (dd-MM-yyyy HH:mm): ");
        String dateTimeStr = scanner.nextLine();

        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DATE_FORMAT);
            doctor.addAvailability(dateTime);
        } catch (Exception e) {
            System.out.println("Invalid date/time format. Please use dd-MM-yyyy HH:mm");
        }
    }

    private static void cancelAppointmentMenu(Doctor doctor, Scanner scanner) {
        doctor.viewSchedule();
        List<Appointment> appointments = doctor.getSchedule();

        if (appointments.isEmpty()) {
            return;
        }

        System.out.print("Enter appointment number to cancel: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;

        if (index >= 0 && index < appointments.size()) {
            doctor.cancelAppointment(appointments.get(index));
        } else {
            System.out.println("Invalid appointment number");
        }
    }
}