package mainpackage;

import java.time.LocalDateTime;

public class Appointment {
    private LocalDateTime dateTime;
    private Doctor doctor;
    private Patient patient;

    public Appointment(LocalDateTime dateTime, Doctor doctor) {
        this.dateTime = dateTime;
        this.doctor = doctor;
        this.patient = null;

    }

    // Getters for all fields
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    @Override
    public String toString() {
        return "Appointment[" +
                "DateTime: " + dateTime +
                ", Doctor: " + doctor.getSurname() +
                ", Patient: " + (patient != null ? patient.getName() : "Available");
    }

    public void bookAppointment(Patient patient) {
        this.patient = patient;
        System.out.println("Appointment booked for " + patient.getName() +
                " with Dr. " + doctor.getSurname());
    }
    //cancel an appointment from the doctor class
}