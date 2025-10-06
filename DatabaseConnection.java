package com.company.inventory3.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Absolute path to avoid accidental multiple DB files
    private static final String URL = "jdbc:sqlite:/home/iit/databases/inventory.db";

    private static DatabaseConnection instance;
    private static Connection connection;

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC"); // Load driver
            connection = DriverManager.getConnection(URL);
            connection.setAutoCommit(true); // ensure updates are saved automatically
            System.out.println("Database connected successfully!");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite driver not found", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null || connection == null || connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public static Connection getDBConnection() {
        try {
            return getInstance().getConnection();
        } catch (SQLException e) {
            System.err.println("Failed to get database connection: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    // ✅ Safely close the database connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
            instance = null; // reset singleton so a new one can be created later if needed
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
