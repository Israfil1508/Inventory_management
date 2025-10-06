package com.company.inventory3.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Use absolute path to ensure the same database is used everywhere
    private static final String URL = "jdbc:sqlite:/home/iit/databases/inventory.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
