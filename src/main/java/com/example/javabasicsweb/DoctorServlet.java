package com.example.javabasicsweb;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mainpackage.Appointment;
import mainpackage.Doctor;
import mainpackage.Patient;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "DoctorServlet", urlPatterns = {"/doctor"})
public class DoctorServlet extends HttpServlet {
    private boolean isDoctor(HttpSession session) {
        return session != null && session.getAttribute("userId") != null && "DOCTOR".equals(session.getAttribute("type"));
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
                    if (isDoctor(session)) viewDoctorDetails(request, response);
                    else response.sendRedirect("doctor?action=login");
                    break;
                case "schedule":
                    if (isDoctor(session)) viewSchedule(request, response);
                    else response.sendRedirect("doctor?action=login");
                    break;
                case "addAvailability":
                    if (isDoctor(session)) showAddAvailabilityForm(request, response);
                    else response.sendRedirect("doctor?action=login");
                    break;
                case "addMonthlyAvailability":
                    if (isDoctor(session)) showAddMonthlyAvailabilityForm(request, response);
                    else response.sendRedirect("doctor?action=login");
                    break;
                case "cancel":
                    if (isDoctor(session)) cancelAppointment(request, response);
                    else response.sendRedirect("doctor?action=login");
                    break;
                case "logout":
                    if (session != null) session.invalidate();
                    response.sendRedirect("doctor?action=login");
                    break;
                case "register":
                    showRegistrationForm(request, response);
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
                loginDoctor(request, response);
            } else if ("register".equals(action)) {
                registerUser(request, response);
            } else if ("addAvailability".equals(action)) {
                addAvailability(request, response);
            } else if ("addMonthlyAvailability".equals(action)) {
                addMonthlyAvailability(request, response);
            } else {
                doGet(request, response);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + ex.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher(action.equals("register") ? "/register.jsp" : "/index.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void showLoginForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request, response);
    }

    private void showRegistrationForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
        dispatcher.forward(request, response);
    }

    private void showAddAvailabilityForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/addAvailability.jsp");
        dispatcher.forward(request, response);
    }

    private void showAddMonthlyAvailabilityForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/addMonthlyAvailability.jsp");
        dispatcher.forward(request, response);
    }

    private void loginDoctor(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
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
                    response.sendRedirect("doctor?action=view");
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

    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String ageStr = request.getParameter("age");
        String type = request.getParameter("type");
        String specialty = request.getParameter("specialty");
        String amkaStr = request.getParameter("amka");

        if (username == null || password == null || name == null || surname == null || ageStr == null || type == null ||
                username.trim().isEmpty() || password.trim().isEmpty() || name.trim().isEmpty() || surname.trim().isEmpty() || ageStr.trim().isEmpty() || type.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Παρακαλώ συμπληρώστε όλα τα υποχρεωτικά πεδία.");
            showRegistrationForm(request, response);
            return;
        }

        if ("DOCTOR".equals(type) && (specialty == null || specialty.trim().isEmpty())) {
            request.setAttribute("errorMessage", "Η ειδικότητα είναι υποχρεωτική για ιατρούς.");
            showRegistrationForm(request, response);
            return;
        }

        if ("PATIENT".equals(type) && (amkaStr == null || amkaStr.trim().isEmpty())) {
            request.setAttribute("errorMessage", "Το AMKA είναι υποχρεωτικό για ασθενείς.");
            showRegistrationForm(request, response);
            return;
        }

        int age;
        long amka = 0;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0) throw new NumberFormatException();
            if ("PATIENT".equals(type)) {
                amka = Long.parseLong(amkaStr);
                if (amka <= 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Η ηλικία και το AMKA πρέπει να είναι έγκυροι αριθμοί.");
            showRegistrationForm(request, response);
            return;
        }

        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO users (username, password, name, surname, age, type) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, name);
                stmt.setString(4, surname);
                stmt.setInt(5, age);
                stmt.setString(6, type);
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    if ("DOCTOR".equals(type)) {
                        try (PreparedStatement doctorStmt = conn.prepareStatement(
                                "INSERT INTO doctors (user_id, specialty) VALUES (?, ?)")) {
                            doctorStmt.setInt(1, userId);
                            doctorStmt.setString(2, specialty);
                            doctorStmt.executeUpdate();
                        }
                    } else if ("PATIENT".equals(type)) {
                        try (PreparedStatement patientStmt = conn.prepareStatement(
                                "INSERT INTO patients (user_id, amka) VALUES (?, ?)")) {
                            patientStmt.setInt(1, userId);
                            patientStmt.setLong(2, amka);
                            patientStmt.executeUpdate();
                        }
                    }
                    conn.commit();
                    request.setAttribute("successMessage", "Η εγγραφή ολοκληρώθηκε επιτυχώς!");
                }
            } catch (SQLException e) {
                conn.rollback();
                request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + e.getMessage());
                showRegistrationForm(request, response);
                return;
            }
        }
        response.sendRedirect("doctor?action=login");
    }

    private void viewDoctorDetails(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("doctor?action=login");
            return;
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.name, u.surname, u.username, u.age, d.specialty FROM users u JOIN doctors d ON u.id = d.user_id WHERE u.id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("name", rs.getString("name"));
                request.setAttribute("surname", rs.getString("surname"));
                request.setAttribute("username", rs.getString("username"));
                request.setAttribute("age", rs.getInt("age"));
                request.setAttribute("specialty", rs.getString("specialty"));
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher("/doctorDetails.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void viewSchedule(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("doctor?action=login");
            return;
        }

        List<Appointment> appointments = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT a.id, a.date_time, a.status, u.name AS patient_name, u.surname AS patient_surname, p.user_id AS patient_id " +
                             "FROM appointments a " +
                             "LEFT JOIN patients p ON a.patient_id = p.user_id " +
                             "LEFT JOIN users u ON p.user_id = u.id " +
                             "WHERE a.doctor_id = ? " +
                             "ORDER BY a.date_time DESC")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Appointment appt = new Appointment(rs.getTimestamp("date_time").toLocalDateTime(), null);
                appt.setId(rs.getInt("id"));
                appt.setStatus(rs.getString("status"));
                if (rs.getObject("patient_id") != null) {
                    Patient patient = new Patient(0, "", "", rs.getString("patient_name"), rs.getString("patient_surname"), 0, 0L, "PATIENT");
                    appt.setPatient(patient);
                }
                appointments.add(appt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Σφάλμα κατά την ανάκτηση προγράμματος: " + e.getMessage());
        }

        request.setAttribute("appointments", appointments);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/doctorSchedule.jsp");
        dispatcher.forward(request, response);
    }

    private void addAvailability(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        String dateTimeStr = request.getParameter("dateTime");

        if (userId == null || dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Παρακαλώ συμπληρώστε την ημερομηνία και ώρα.");
            showAddAvailabilityForm(request, response);
            return;
        }

        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateTimeStr);
            if (dateTime.isBefore(LocalDateTime.now())) {
                request.setAttribute("errorMessage", "Η ημερομηνία πρέπει να είναι στο μέλλον.");
                showAddAvailabilityForm(request, response);
                return;
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Μη έγκυρη μορφή ημερομηνίας (YYYY-MM-DD HH:MM).");
            showAddAvailabilityForm(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO appointments (date_time, doctor_id, status) VALUES (?, ?, 'AVAILABLE')")) {
            stmt.setTimestamp(1, Timestamp.valueOf(dateTime));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            request.setAttribute("successMessage", "Η διαθεσιμότητα προστέθηκε επιτυχώς!");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Σφάλμα κατά την προσθήκη διαθεσιμότητας: " + e.getMessage());
        }
        response.sendRedirect("doctor?action=schedule");
    }

    private void addMonthlyAvailability(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        String yearMonthStr = request.getParameter("yearMonth");
        String days = request.getParameter("days");
        String times = request.getParameter("times");

        if (userId == null || yearMonthStr == null || days == null || times == null ||
                yearMonthStr.trim().isEmpty() || days.trim().isEmpty() || times.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Παρακαλώ συμπληρώστε όλα τα πεδία.");
            showAddMonthlyAvailabilityForm(request, response);
            return;
        }

        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(yearMonthStr);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Μη έγκυρη μορφή έτους-μήνα (YYYY-MM).");
            showAddMonthlyAvailabilityForm(request, response);
            return;
        }

        String[] dayArray = days.split(",");
        String[] timeArray = times.split(",");
        List<LocalDateTime> dateTimes = new ArrayList<>();
        for (String day : dayArray) {
            try {
                int d = Integer.parseInt(day.trim());
                for (String time : timeArray) {
                    LocalDateTime dateTime = LocalDateTime.parse(yearMonthStr + "-" + String.format("%02d", d) + "T" + time.trim());
                    if (dateTime.isAfter(LocalDateTime.now())) {
                        dateTimes.add(dateTime);
                    }
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Μη έγκυρη ημέρα ή ώρα.");
                showAddMonthlyAvailabilityForm(request, response);
                return;
            }
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO appointments (date_time, doctor_id, status) VALUES (?, ?, 'AVAILABLE')")) {
                for (LocalDateTime dateTime : dateTimes) {
                    stmt.setTimestamp(1, Timestamp.valueOf(dateTime));
                    stmt.setInt(2, userId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
                conn.commit();
                request.setAttribute("successMessage", "Η μηνιαία διαθεσιμότητα προστέθηκε επιτυχώς!");
            } catch (SQLException e) {
                conn.rollback();
                request.setAttribute("errorMessage", "Σφάλμα κατά την προσθήκη διαθεσιμότητας: " + e.getMessage());
                showAddMonthlyAvailabilityForm(request, response);
                return;
            }
        }
        response.sendRedirect("doctor?action=schedule");
    }

    private void cancelAppointment(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        String appointmentIdStr = request.getParameter("appointmentId");

        if (userId == null || appointmentIdStr == null || appointmentIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Το ID του ραντεβού είναι υποχρεωτικό.");
            viewSchedule(request, response);
            return;
        }

        int appointmentId;
        try {
            appointmentId = Integer.parseInt(appointmentIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Μη έγκυρο ID ραντεβού.");
            viewSchedule(request, response);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysLater = now.plusDays(3);

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT date_time FROM appointments WHERE id = ? AND doctor_id = ?")) {
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
                        request.setAttribute("errorMessage", "Το ραντεβού μπορεί να ακυρωθεί μόνο αν είναι 3+ ημέρες πριν.");
                    }
                } else {
                    request.setAttribute("errorMessage", "Το ραντεβού δεν βρέθηκε ή δεν σας ανήκει.");
                }
            } catch (SQLException e) {
                conn.rollback();
                request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + e.getMessage());
            }
        }
        viewSchedule(request, response);
    }
}