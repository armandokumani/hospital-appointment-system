package mainpackage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Patient extends Users {
    private final int AMKA;
    private List<Appointment> appointments;

    public Patient(String username, String password, String name, String surname, int age, int AMKA) {
        super(username, password, name, surname, age);
        this.AMKA = AMKA;
        this.appointments = new ArrayList<>();
    }

    // Getter for AMKA (no setter as it's final)
    public int getAMKA() {
        return AMKA;
    }
    // Getter for appointments
    public List<Appointment> getAppointments() {
        return appointments;
    }
    @Override
    public String toString() {
        return "Patient[" +
                "Username: " + username +
                ", Name: " + name + " " + surname +
                ", Age: " + age +
                ", AMKA: " + AMKA + "]";
    }
    public void registrationPatient() {
        System.out.println("Patient with AMKA: " + AMKA + " registered successfully");
    }

    public void viewUpcomingAppointments() {
        System.out.println("\n=== UPCOMING APPOINTMENTS ===");
        if (appointments.isEmpty()) {
            System.out.println("No upcoming appointments");
        } else {
            appointments.stream()
                    .filter(a -> a.getDateTime().isAfter(LocalDateTime.now()))
                    .forEach(app -> System.out.println(app.getDateTime() + " with Dr. " +
                            app.getDoctor().getSurname()));
        }
    }

    public void viewAppointmentHistory() {
        System.out.println("\n=== APPOINTMENT HISTORY ===");
        if (appointments.isEmpty()) {
            System.out.println("No past appointments");
        } else {
            appointments.stream()
                    .filter(a -> a.getDateTime().isBefore(LocalDateTime.now()))
                    .forEach(app -> System.out.println(app.getDateTime() + " with Dr. " +
                            app.getDoctor().getSurname()));
        }
    }

    public void bookAppointment(Appointment appointment) {
        if (appointment.getPatient() == null) {
            appointment.bookAppointment(this);
            appointments.add(appointment);
            System.out.println("Appointment booked successfully!");
        } else {
            System.out.println("This time slot is already booked");
        }
    }

}