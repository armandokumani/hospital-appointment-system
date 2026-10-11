package com.example.javabasicsweb;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mainpackage.Appointment;
import mainpackage.Doctor;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PatientServlet", urlPatterns = {"/patient"})
public class PatientServlet extends HttpServlet {
    private boolean isPatient(HttpSession session) {
        return session != null && session.getAttribute("userId") != null && "PATIENT".equals(session.getAttribute("type"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "login";
        HttpSession session = request.getSession(false);

        try {
            switch (action) {
                case "login":
                    showLoginForm(request, response);
                    break;
                case "view":
                    if (isPatient(session)) viewPatientDetails(request, response);
                    else response.sendRedirect("patient?action=login");
                    break;
                case "history":
                    if (isPatient(session)) viewAppointmentHistory(request, response);
                    else response.sendRedirect("patient?action=login");
                    break;
                case "available":
                    if (isPatient(session)) viewAvailableAppointments(request, response);
                    else response.sendRedirect("patient?action=login");
                    break;
                case "book":
                    if (isPatient(session)) bookAppointment(request, response);
                    else response.sendRedirect("patient?action=login");
                    break;
                case "cancel":
                    if (isPatient(session)) cancelAppointment(request, response);
                    else response.sendRedirect("patient?action=login");
                    break;
                case "logout":
                    if (session != null) session.invalidate();
                    response.sendRedirect("patient?action=login");
                    break;
                default:
                    showLoginForm(request, response);
                    break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + ex.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("login".equals(action)) {
                loginPatient(request, response);
            } else {
                doGet(request, response);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + ex.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void showLoginForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    private void loginPatient(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Παρακαλώ συμπληρώστε όλα τα πεδία.");
            showLoginForm(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.id, u.password, u.type FROM users u WHERE u.username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("userId", rs.getInt("id"));
                    session.setAttribute("type", rs.getString("type"));
                    session.setAttribute("username", username);
                    response.sendRedirect("patient?action=view");
                } else {
                    request.setAttribute("errorMessage", "Λάθος κωδικός ή όνομα χρήστη.");
                    showLoginForm(request, response);
                }
            } else {
                request.setAttribute("errorMessage", "Λάθος κωδικός ή όνομα χρήστη.");
                showLoginForm(request, response);
            }
        }
    }

    private void viewPatientDetails(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("patient");
            return;
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.name, u.surname, u.username, u.age, p.amka FROM users u JOIN patients p ON u.id = p.user_id WHERE u.id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("name", rs.getString("name"));
                request.setAttribute("surname", rs.getString("surname"));
                request.setAttribute("username", rs.getString("username"));
                request.setAttribute("age", rs.getInt("age"));
                request.setAttribute("amka", rs.getLong("amka"));
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher("/patientDetails.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void viewAppointmentHistory(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("patient");
            return;
        }

        List<Appointment> appointments = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT a.id, a.date_time, a.status, u.name AS doctor_name, u.surname AS doctor_surname, d.specialty " +
                             "FROM appointments a " +
                             "JOIN doctors d ON a.doctor_id = d.user_id " +
                             "JOIN users u ON d.user_id = u.id " +
                             "WHERE a.patient_id = ? " +
                             "ORDER BY a.date_time DESC")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Doctor doctor = new Doctor(0, "", "", rs.getString("doctor_name"), rs.getString("doctor_surname"), 0, rs.getString("specialty"), "DOCTOR");
                LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
                Appointment appt = new Appointment(dateTime, doctor);
                appt.setId(rs.getInt("id"));
                appt.setStatus(rs.getString("status"));
                appointments.add(appt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Σφάλμα κατά την ανάκτηση ραντεβού: " + e.getMessage());
        }

        request.setAttribute("appointments", appointments);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/appointmentHistory.jsp");
        dispatcher.forward(request, response);
    }

    private void viewAvailableAppointments(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("patient");
            return;
        }

        String specialty = request.getParameter("specialty");
        List<Appointment> availableAppointments = new ArrayList<>();
        String query = "SELECT a.id, a.date_time, u.name AS doctor_name, u.surname AS doctor_surname, d.specialty " +
                "FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.user_id " +
                "JOIN users u ON d.user_id = u.id " +
                "WHERE a.status = 'AVAILABLE' AND a.date_time > NOW()";
        if (specialty != null && !specialty.trim().isEmpty()) {
            query += " AND d.specialty = ?";
        }
        query += " ORDER BY a.date_time ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            if (specialty != null && !specialty.trim().isEmpty()) {
                stmt.setString(1, specialty);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Doctor doctor = new Doctor(0, "", "", rs.getString("doctor_name"), rs.getString("doctor_surname"), 0, rs.getString("specialty"), "DOCTOR");
                LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
                Appointment appt = new Appointment(dateTime, doctor);
                appt.setId(rs.getInt("id"));
                appt.setStatus("AVAILABLE");
                availableAppointments.add(appt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Σφάλμα κατά την ανάκτηση διαθέσιμων ραντεβού: " + e.getMessage());
        }

        request.setAttribute("availableAppointments", availableAppointments);
        request.setAttribute("specialty", specialty);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/availableAppointments.jsp");
        dispatcher.forward(request, response);
    }

    private void bookAppointment(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        String appointmentIdStr = request.getParameter("appointmentId");

        if (userId == null || appointmentIdStr == null || appointmentIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Το ID του ραντεβού είναι υποχρεωτικό.");
            viewAvailableAppointments(request, response);
            return;
        }

        int appointmentId;
        try {
            appointmentId = Integer.parseInt(appointmentIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Μη έγκυρο ID ραντεβού.");
            viewAvailableAppointments(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE appointments SET patient_id = ?, status = 'BOOKED' WHERE id = ? AND status = 'AVAILABLE'")) {
                stmt.setInt(1, userId);
                stmt.setInt(2, appointmentId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    conn.commit();
                    request.setAttribute("successMessage", "Το ραντεβού κλείστηκε επιτυχώς!");
                } else {
                    conn.rollback();
                    request.setAttribute("errorMessage", "Το ραντεβού δεν είναι διαθέσιμο.");
                }
            } catch (SQLException e) {
                conn.rollback();
                request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + e.getMessage());
            }
        }
        viewAvailableAppointments(request, response);
    }

    private void cancelAppointment(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        String appointmentIdStr = request.getParameter("appointmentId");

        if (userId == null || appointmentIdStr == null || appointmentIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Το ID του ραντεβού είναι υποχρεωτικό.");
            viewAppointmentHistory(request, response);
            return;
        }

        int appointmentId;
        try {
            appointmentId = Integer.parseInt(appointmentIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Μη έγκυρο ID ραντεβού.");
            viewAppointmentHistory(request, response);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT date_time FROM appointments WHERE id = ? AND patient_id = ?")) {
                stmt.setInt(1, appointmentId);
                stmt.setInt(2, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    LocalDateTime appointmentTime = rs.getTimestamp("date_time").toLocalDateTime();
                    if (appointmentTime.isAfter(threeDaysLater)) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(
                                "UPDATE appointments SET patient_id = NULL, status = 'AVAILABLE' WHERE id = ?")) {
                            updateStmt.setInt(1, appointmentId);
                            int rows = updateStmt.executeUpdate();
                            if (rows > 0) {
                                conn.commit();
                                request.setAttribute("successMessage", "Το ραντεβού ακυρώθηκε επιτυχώς!");
                            } else {
                                conn.rollback();
                                request.setAttribute("errorMessage", "Σφάλμα κατά την ακύρωση.");
                            }
                        }
                    } else {
                        conn.rollback();
                        request.setAttribute("errorMessage", "Το ραντεβού μπορεί να ακυρωθεί μόνο αν είναι 3+ ημέρες πριν.");
                    }
                } else {
                    conn.rollback();
                    request.setAttribute("errorMessage", "Το ραντεβού δεν βρέθηκε ή δεν σας ανήκει.");
                }
            } catch (SQLException e) {
                conn.rollback();
                request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + e.getMessage());
            }
        }
        viewAppointmentHistory(request, response);
    }
}