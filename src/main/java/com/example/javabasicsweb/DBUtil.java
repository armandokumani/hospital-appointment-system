package com.example.javabasicsweb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database utility for managing MySQL JDBC connections.
 * Credentials can be configured via environment variables or JVM system properties.
 */
public class DBUtil {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/login_schema?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final String DB_URL = getEnvOrProperty("DB_URL", "db.url", DEFAULT_URL);
    private static final String DB_USER = getEnvOrProperty("DB_USER", "db.user", DEFAULT_USER);
    private static final String DB_PASSWORD = getEnvOrProperty("DB_PASSWORD", "db.password", DEFAULT_PASSWORD);

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    private static String getEnvOrProperty(String envKey, String propKey, String defaultValue) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal;
        }
        return System.getProperty(propKey, defaultValue);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
