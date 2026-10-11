package mainpackage;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private LocalDateTime dateTime;
    private Doctor doctor;
    private Patient patient;
    private String status;

    public Appointment(LocalDateTime dateTime, Doctor doctor) {
        this.dateTime = dateTime;
        this.doctor = doctor;
        this.patient = null;
        this.status = "AVAILABLE";
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        this.status = (patient != null) ? "BOOKED" : "AVAILABLE";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Appointment[" +
                "DateTime: " + dateTime +
                ", Doctor: " + (doctor != null ? doctor.getSurname() : "N/A") +
                ", Patient: " + (patient != null ? patient.getName() : "Available") +
                ", Status: " + status + "]";
    }

    public void bookAppointment(Patient patient) {
        this.patient = patient;
        this.status = "BOOKED";
    }
}