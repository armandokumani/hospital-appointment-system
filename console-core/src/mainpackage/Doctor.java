package mainpackage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends Users {
    private String specialty;
    private List<Appointment> schedule;

    public Doctor(String username, String password, String name, String surname, int age, String specialty) {
        super(username, password, name, surname, age);
        this.specialty = specialty;
        this.schedule = new ArrayList<>();
    }

    // Getter for schedule
    public List<Appointment> getSchedule() {
        return schedule;
    }
    @Override
    public String toString() {
        return "Doctor[" +
                "Username: " + username +
                ", Name: " + name + " " + surname +
                ", Age: " + age +
                ", Specialty: " + specialty + "]";
    }

    public List<Appointment> getAvailableAppointments() {
        List<Appointment> available = new ArrayList<>();
        for (Appointment app : schedule) {
            if (app.getPatient() == null && app.getDateTime().isAfter(LocalDateTime.now())) {
                available.add(app);
            }
        }
        return available;
    }

    public void addAvailability(LocalDateTime dateTime) {
        Appointment newAppointment = new Appointment(dateTime, this);
        schedule.add(newAppointment);
        System.out.println("Added availability on " + dateTime);
    }

    public void viewSchedule() {
        System.out.println("\n=== DR. " + surname.toUpperCase() + "'S SCHEDULE ===");
        if (schedule.isEmpty()) {
            System.out.println("No appointments scheduled");
        } else {
            schedule.forEach(app -> {
                String status = app.getPatient() == null ? "Available" :
                        "Booked by " + app.getPatient().getName();
                System.out.println(app.getDateTime() + " - " + status);
            });
        }
    }

    public void cancelAppointment(Appointment appointment) {
        if (schedule.contains(appointment)) {
            if (appointment.getPatient() != null) {
                Patient patient = appointment.getPatient();
                patient.getAppointments().remove(appointment);
            }
            schedule.remove(appointment);
            System.out.println("Appointment canceled successfully");
        } else {
            System.out.println("Appointment not found in schedule");
        }
    }

}