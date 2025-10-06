package com.company.inventory3.database;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createDatabaseAndTables() {
        // Ensure folder exists
        File dbDir = new File("/home/iit/databases");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }

        String createEmployeeTable = """
                CREATE TABLE IF NOT EXISTS employee (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    address TEXT,
                    password TEXT NOT NULL,
                    role TEXT CHECK(role IN ('ADMIN','EMPLOYEE')) DEFAULT 'EMPLOYEE',
                    created_at TEXT DEFAULT (datetime('now'))
                );
                """;

        String createCustomerTable = """
                CREATE TABLE IF NOT EXISTS customer (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    total_cost DECIMAL(10,2) DEFAULT 0.00,
                    created_at TEXT DEFAULT (datetime('now'))
                );
                """;

        String createCategoryTable = """
                CREATE TABLE IF NOT EXISTS category (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type TEXT NOT NULL UNIQUE
                );
                """;

        String createProductTable = """
                CREATE TABLE IF NOT EXISTS product (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    quantity INTEGER NOT NULL DEFAULT 0,
                    category_id INTEGER,
                    created_at TEXT DEFAULT (datetime('now')),
                    FOREIGN KEY (category_id) REFERENCES category(id)
                );
                """;

        String createOrderTable = """
                CREATE TABLE IF NOT EXISTS view_order (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customer_id INTEGER NOT NULL,
                    customer_name TEXT NOT NULL,
                    product_id INTEGER NOT NULL,
                    product_name TEXT NOT NULL,
                    quantity_ordered INTEGER NOT NULL,
                    unit_price DECIMAL(10,2) NOT NULL,
                    total_price DECIMAL(10,2) NOT NULL,
                    order_date TEXT DEFAULT (datetime('now')),
                    created_by_employee_id INTEGER,
                    FOREIGN KEY (customer_id) REFERENCES customer(id),
                    FOREIGN KEY (product_id) REFERENCES product(id),
                    FOREIGN KEY (created_by_employee_id) REFERENCES employee(id)
                );
                """;

        String insertAdmin = """
                INSERT OR IGNORE INTO employee (name, email, address, password, role)
                VALUES ('Admin User', 'admin@company.com', 'Main Office', 'admin$1', 'ADMIN');
                """;

        String insertEmployee = """
                INSERT OR IGNORE INTO employee (name, email, address, password, role)
                VALUES ('Employee User', 'employee@company.com', 'Main Office', 'employee$1', 'EMPLOYEE');
                """;

        String insertCategories = """
                INSERT OR IGNORE INTO category (type) VALUES
                ('Electronics'), ('Clothing'), ('Books'), ('Home & Garden'), ('Sports');
                """;

       

        String insertProducts = """
                INSERT OR IGNORE INTO product (name, price, quantity, category_id) VALUES
                ('Laptop', 999.99, 10, 1),
                ('T-Shirt', 19.99, 50, 2),
                ('Java Programming Book', 49.99, 25, 3),
                ('Garden Chair', 79.99, 15, 4),
                ('Tennis Racket', 129.99, 8, 5);
                """;

        String insertCustomers = """
                INSERT OR IGNORE INTO customer (name, email, total_cost) VALUES
                ('John Doe', 'john@email.com', 0.00),
                ('Jane Smith', 'jane@email.com', 0.00),
                ('Bob Johnson', 'bob@email.com', 0.00);
                """;

        try (Connection conn = DatabaseConnection.getDBConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createEmployeeTable);
            stmt.execute(createCustomerTable);
            stmt.execute(createCategoryTable);
            stmt.execute(createProductTable);
            stmt.execute(createOrderTable);

            stmt.execute(insertAdmin);
            String insertEmployees = "";
            stmt.execute(insertEmployees);
            stmt.execute(insertProducts);
            stmt.execute(insertCustomers);

            System.out.println("SQLite database, tables, and sample data created successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
