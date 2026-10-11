package com.example.javabasicsweb;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "login";

        if ("register".equals(action)) {
            showRegistrationForm(request, response);
        } else {
            showLoginForm(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("login".equals(action)) {
                loginUser(request, response);
            } else if ("register".equals(action)) {
                registerUser(request, response);
            } else {
                doGet(request, response);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Σφάλμα βάσης δεδομένων: " + ex.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("register".equals(action) ? "/register.jsp" : "/index.jsp");
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

    private void loginUser(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
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
                    String type = rs.getString("type").toLowerCase();
                    response.sendRedirect(type + "?action=view");
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

        // Input validation
        if (username == null || password == null || name == null || surname == null || ageStr == null || type == null ||
                username.trim().isEmpty() || password.trim().isEmpty() || name.trim().isEmpty() || surname.trim().isEmpty() || ageStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Παρακαλώ συμπληρώστε όλα τα υποχρεωτικά πεδία.");
            showRegistrationForm(request, response);
            return;
        }

        if ("DOCTOR".equalsIgnoreCase(type)) {
            if (specialty == null || specialty.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Η ειδικότητα είναι υποχρεωτική για ιατρούς.");
                showRegistrationForm(request, response);
                return;
            }
        } else if ("PATIENT".equalsIgnoreCase(type)) {
            if (amkaStr == null || amkaStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Το AMKA είναι υποχρεωτικό για ασθενείς.");
                showRegistrationForm(request, response);
                return;
            }
        }

        int age;
        long amka = 0;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0) throw new NumberFormatException();
            if ("PATIENT".equalsIgnoreCase(type)) {
                amka = Long.parseLong(amkaStr);
                if (amka <= 0) throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Η ηλικία και το AMKA πρέπει να είναι έγκυροι αριθμοί.");
            showRegistrationForm(request, response);
            return;
        }

        // Check for duplicate username
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("errorMessage", "Το όνομα χρήστη υπάρχει ήδη.");
                showRegistrationForm(request, response);
                return;
            }
        }

        String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert into users table
                int userId;
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO users (username, password, name, surname, age, type) VALUES (?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, username);
                    stmt.setString(2, hashedPassword);
                    stmt.setString(3, name);
                    stmt.setString(4, surname);
                    stmt.setInt(5, age);
                    stmt.setString(6, type.toUpperCase());
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (!rs.next()) {
                        throw new SQLException("Αποτυχία δημιουργίας χρήστη.");
                    }
                    userId = rs.getInt(1);
                }

                // Insert into doctors or patients table
                if ("DOCTOR".equalsIgnoreCase(type)) {
                    try (PreparedStatement doctorStmt = conn.prepareStatement(
                            "INSERT INTO doctors (user_id, specialty) VALUES (?, ?)")) {
                        doctorStmt.setInt(1, userId);
                        doctorStmt.setString(2, specialty);
                        doctorStmt.executeUpdate();
                    }
                } else if ("PATIENT".equalsIgnoreCase(type)) {
                    try (PreparedStatement patientStmt = conn.prepareStatement(
                            "INSERT INTO patients (user_id, amka) VALUES (?, ?)")) {
                        patientStmt.setInt(1, userId);
                        patientStmt.setLong(2, amka);
                        patientStmt.executeUpdate();
                    }
                }

                conn.commit();
                request.setAttribute("successMessage", "Επιτυχής εγγραφή! Παρακαλώ συνδεθείτε.");
                showLoginForm(request, response);
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                request.setAttribute("errorMessage", "Σφάλμα κατά την εγγραφή: " + e.getMessage());
                showRegistrationForm(request, response);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}