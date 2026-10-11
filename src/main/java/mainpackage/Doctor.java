package mainpackage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends Users {
    private String specialty;
    private List<Appointment> schedule;

    public Doctor(int id, String username, String password, String name, String surname, int age, String specialty, String type) {
        super(id, username, password, name, surname, age, type);
        this.specialty = specialty;
        this.schedule = new ArrayList<>();
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public List<Appointment> getSchedule() {
        return schedule;
    }

    @Override
    public String getDetails() {
        return specialty;
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
                appointment.setPatient(null);
                appointment.setStatus("AVAILABLE");
            }
            schedule.remove(appointment);
        }
    }
}