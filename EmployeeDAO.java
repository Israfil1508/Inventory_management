package com.company.inventory3.dao;

import com.company.inventory3.database.DatabaseConnection;
import com.company.inventory3.model.Employee;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public Employee authenticate(String email, String password) {
        String sql = "SELECT * FROM employee WHERE email = ?";

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                boolean match = false;

                if (storedPassword != null) {
                    if (storedPassword.startsWith("$2a$") ||
                            storedPassword.startsWith("$2b$") ||
                            storedPassword.startsWith("$2y$")) {
                        match = BCrypt.checkpw(password, storedPassword);
                    } else {
                        match = password.equals(storedPassword);
                    }
                }

                if (match) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setName(rs.getString("name"));
                    employee.setEmail(rs.getString("email"));
                    employee.setAddress(rs.getString("address"));
                    employee.setRole(Employee.Role.valueOf(rs.getString("role")));

                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        employee.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    return employee;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error authenticating employee: " + e.getMessage());
        }
        return null;
    }

    public boolean createEmployee(Employee employee) {
        String sql = "INSERT INTO employee (name, email, address, password, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getEmail());
            stmt.setString(3, employee.getAddress());
            stmt.setString(4, BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt()));
            stmt.setString(5, employee.getRole().toString());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating employee: " + e.getMessage());
            return false;
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee ORDER BY name";

        try (Connection conn = DatabaseConnection.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employee.setEmail(rs.getString("email"));
                employee.setAddress(rs.getString("address"));
                employee.setRole(Employee.Role.valueOf(rs.getString("role")));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    employee.setCreatedAt(createdAt.toLocalDateTime());
                }

                employees.add(employee);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
        }
        return employees;
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employee SET name = ?, email = ?, address = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getEmail());
            stmt.setString(3, employee.getAddress());
            stmt.setString(4, employee.getRole().toString());
            stmt.setInt(5, employee.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteEmployee(int employeeId) {
        String sql = "DELETE FROM employee WHERE id = ?";

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    public Employee getEmployeeById(int id) {
        String sql = "SELECT * FROM employee WHERE id = ?";

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employee.setEmail(rs.getString("email"));
                employee.setAddress(rs.getString("address"));
                employee.setRole(Employee.Role.valueOf(rs.getString("role")));

                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    employee.setCreatedAt(createdAt.toLocalDateTime());
                }

                return employee;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching employee by ID: " + e.getMessage());
        }
        return null;
    }
}
