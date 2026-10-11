package com.example.javabasicsweb;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mainpackage.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {
    private boolean isAdmin(HttpSession session) {
        return session != null && session.getAttribute("userId") != null && "ADMIN".equals(session.getAttribute("type"));
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
                    if (isAdmin(session)) viewAdminDetails(request, response);
                    else response.sendRedirect("admin?action=login");
                    break;
                case "addDoctor":
                    if (isAdmin(session)) showAddDoctorForm(request, response);
                    else response.sendRedirect("admin?action=login");
                    break;
                case "addUser":
                    if (isAdmin(session)) showAddUserForm(request, response);
                    else response.sendRedirect("admin?action=login");
                    break;
                case "deleteDoctor":
                    if (isAdmin(session)) listDoctors(request, response);
                    else response.sendRedirect("admin?action=login");
                    break;
                case "deletePatients":
                    if (isAdmin(session)) listPatients(request, response);
                    else response.sendRedirect("admin?action=login");
                    break;
                case "viewUsers":
                    if (isAdmin(session)) listUsers(request, response);
                    else response.sendRedirect("admin?action=login");
                    break;
                case "logout":
                    if (session != null) session.invalidate();
                    response.sendRedirect("admin?action=login");
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
                loginAdmin(request, response);
            } else if ("addDoctor".equals(action)) {
                addDoctor(request, response);
            } else if ("addUser".equals(action)) {
                addUser(request, response);
            } else if ("deleteDoctor".equals(action)) {
                deleteDoctor(request, response);
            } else if ("deletePatients".equals(action)) {
                deletePatients(request, response);
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

    private void showAddDoctorForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/addDoctor.jsp");
        dispatcher.forward(request, response);
    }

    private void showAddUserForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/addUser.jsp");
        dispatcher.forward(request, response);
    }

    private void loginAdmin(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
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
                if (at.favre.lib.crypto.bcrypt.BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("userId", rs.getInt("id"));
                    session.setAttribute("type", rs.getString("type"));
                    session.setAttribute("username", username);
                    response.sendRedirect("admin?action=view");
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

    private void viewAdminDetails(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("admin?action=login");
            return;
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.name, u.surname, u.username, u.age FROM users u WHERE u.id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("name", rs.getString("name"));
                request.setAttribute("surname", rs.getString("surname"));
                request.setAttribute("username", rs.getString("username"));
                request.setAttribute("age", rs.getInt("age"));
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher("/adminDetails.jsp");
            dispatcher.forward(request, response);
        }
    }

    private void addDoctor(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            response.sendRedirect("admin?action=login");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String ageStr = request.getParameter("age");
        String specialty = request.getParameter("specialty");

        if (username == null || password == null || name == null || surname == null || ageStr == null || specialty == null ||
                username.trim().isEmpty() || password.trim().isEmpty() || name.trim().isEmpty() || surname.trim().isEmpty() || ageStr.trim().isEmpty() || specialty.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Παρακαλώ συμπληρώστε όλα τα πεδία.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/addDoctor.jsp");
            dispatcher.forward(request, response);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Η ηλικία πρέπει να είναι έγκυρος αριθμός.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/addDoctor.jsp");
            dispatcher.forward(request, response);
            return;
        }

        String hashedPassword = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, password.toCharArray());
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
                stmt.setString(6, "DOCTOR");
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    try (PreparedStatement doctorStmt = conn.prepareStatement(
                            "INSERT INTO doctors (user_id, specialty) VALUES (?, ?)")) {
                        doctorStmt.setInt(1, userId);
                        doctorStmt.setString(2, specialty);
                        doctorStmt.executeUpdate();
                        conn.commit();
                        request.setAttribute("successMessage", "Ο γιατρός προστέθηκε επιτυχώς!");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + e.getMessage());
                RequestDispatcher dispatcher = request.getRequestDispatcher("/addDoctor.jsp");
                dispatcher.forward(request, response);
                return;
            }
        }
        response.sendRedirect("admin?action=view");
    }

    private void addUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            response.sendRedirect("admin?action=login");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String ageStr = request.getParameter("age");
        String type = request.getParameter("type");
        String amkaStr = request.getParameter("amka");

        if (username == null || password == null || name == null || surname == null || ageStr == null || type == null ||
                username.trim().isEmpty() || password.trim().isEmpty() || name.trim().isEmpty() || surname.trim().isEmpty() || ageStr.trim().isEmpty() || type.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Παρακαλώ συμπληρώστε όλα τα πεδία.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/addUser.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if ("PATIENT".equals(type) && (amkaStr == null || amkaStr.trim().isEmpty())) {
            request.setAttribute("errorMessage", "Το AMKA είναι υποχρεωτικό για ασθενείς.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/addUser.jsp");
            dispatcher.forward(request, response);
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
            RequestDispatcher dispatcher = request.getRequestDispatcher("/addUser.jsp");
            dispatcher.forward(request, response);
            return;
        }

        String hashedPassword = at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, password.toCharArray());
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
                    if ("PATIENT".equals(type)) {
                        try (PreparedStatement patientStmt = conn.prepareStatement(
                                "INSERT INTO patients (user_id, amka) VALUES (?, ?)")) {
                            patientStmt.setInt(1, userId);
                            patientStmt.setLong(2, amka);
                            patientStmt.executeUpdate();
                        }
                    }
                    conn.commit();
                    request.setAttribute("successMessage", "Ο χρήστης προστέθηκε επιτυχώς!");
                }
            } catch (SQLException e) {
                conn.rollback();
                request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + e.getMessage());
                RequestDispatcher dispatcher = request.getRequestDispatcher("/addUser.jsp");
                dispatcher.forward(request, response);
                return;
            }
        }
        response.sendRedirect("admin?action=view");
    }

    private void deleteDoctor(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            response.sendRedirect("admin?action=login");
            return;
        }

        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Το ID του ιατρού είναι υποχρεωτικό.");
            listDoctors(request, response);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Το ID του ιατρού πρέπει να είναι έγκυρος αριθμός.");
            listDoctors(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM doctors WHERE user_id = ?")) {
                stmt.setInt(1, userId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    try (PreparedStatement userStmt = conn.prepareStatement(
                            "DELETE FROM users WHERE id = ?")) {
                        userStmt.setInt(1, userId);
                        userStmt.executeUpdate();
                        conn.commit();
                        request.setAttribute("successMessage", "Ο γιατρός διαγράφηκε επιτυχώς!");
                    }
                } else {
                    conn.rollback();
                    request.setAttribute("errorMessage", "Ο γιατρός δεν βρέθηκε.");
                }
            } catch (SQLException e) {
                conn.rollback();
                request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + e.getMessage());
            }
        }
        listDoctors(request, response);
    }

    private void listDoctors(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<Doctor> doctors = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.id, u.name, u.surname, u.username, u.age, d.specialty FROM users u JOIN doctors d ON u.id = d.user_id WHERE u.type = 'DOCTOR'")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Doctor doctor = new Doctor(
                        rs.getInt("id"),
                        rs.getString("username"),
                        "",
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getInt("age"),
                        rs.getString("specialty"),
                        "DOCTOR"
                );
                doctors.add(doctor);
            }
        }
        request.setAttribute("doctors", doctors);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/deleteDoctor.jsp");
        dispatcher.forward(request, response);
    }

    private void deletePatients(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isAdmin(session)) {
            response.sendRedirect("admin?action=login");
            return;
        }

        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Το ID του ασθενή είναι υποχρεωτικό.");
            listPatients(request, response);
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Το ID του ασθενή πρέπει να είναι έγκυρος αριθμός.");
            listPatients(request, response);
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM patients WHERE user_id = ?")) {
                stmt.setInt(1, userId);
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    try (PreparedStatement userStmt = conn.prepareStatement(
                            "DELETE FROM users WHERE id = ?")) {
                        userStmt.setInt(1, userId);
                        userStmt.executeUpdate();
                        conn.commit();
                        request.setAttribute("successMessage", "Ο ασθενής διαγράφηκε επιτυχώς!");
                    }
                } else {
                    conn.rollback();
                    request.setAttribute("errorMessage", "Ο ασθενής δεν βρέθηκε.");
                }
            } catch (SQLException e) {
                conn.rollback();
                request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + e.getMessage());
            }
        }
        listPatients(request, response);
    }

    private void listPatients(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<Patient> patients = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.id, u.name, u.surname, u.username, u.age, p.amka FROM users u JOIN patients p ON u.id = p.user_id WHERE u.type = 'PATIENT'")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("username"),
                        "",
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getInt("age"),
                        rs.getLong("amka"),
                        "PATIENT"
                );
                patients.add(patient);
            }
        }
        request.setAttribute("patients", patients);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/deletePatients.jsp");
        dispatcher.forward(request, response);
    }

    private void listUsers(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        List<Users> users = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.id, u.username, u.name, u.surname, u.age, u.type, " +
                             "d.specialty, p.amka " +
                             "FROM users u " +
                             "LEFT JOIN doctors d ON u.id = d.user_id " +
                             "LEFT JOIN patients p ON u.id = p.user_id")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                Users user;
                int id = rs.getInt("id");
                switch (type) {
                    case "PATIENT":
                        user = new Patient(
                                id,
                                rs.getString("username"),
                                "",
                                rs.getString("name"),
                                rs.getString("surname"),
                                rs.getInt("age"),
                                rs.getLong("amka"),
                                type
                        );
                        break;
                    case "DOCTOR":
                        user = new Doctor(
                                id,
                                rs.getString("username"),
                                "",
                                rs.getString("name"),
                                rs.getString("surname"),
                                rs.getInt("age"),
                                rs.getString("specialty"),
                                type
                        );
                        break;
                    case "ADMIN":
                        user = new Admin(
                                id,
                                rs.getString("username"),
                                "",
                                rs.getString("name"),
                                rs.getString("surname"),
                                rs.getInt("age"),
                                type
                        );
                        break;
                    default:
                        user = new Users(
                                id,
                                rs.getString("username"),
                                "",
                                rs.getString("name"),
                                rs.getString("surname"),
                                rs.getInt("age"),
                                type
                        ) {
                            @Override
                            public String getDetails() {
                                return "N/A";
                            }
                        };
                        break;
                }
                users.add(user);
            }
        }
        request.setAttribute("users", users);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/viewUsers.jsp");
        dispatcher.forward(request, response);
    }
}