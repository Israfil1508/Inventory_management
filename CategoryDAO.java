package com.company.inventory3.dao;

import com.company.inventory3.database.DatabaseConnection;
import com.company.inventory3.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public boolean createCategory(Category category) {
        if (category == null || category.getType() == null || category.getType().trim().isEmpty()) {
            System.err.println("Category name cannot be null or empty!");
            return false;
        }

        String sql = "INSERT INTO category (type) VALUES (?)";

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getType().trim());
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting category: " + e.getMessage());
            return false;
        }
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category ORDER BY type";

        try (Connection conn = DatabaseConnection.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setType(rs.getString("type"));
                categories.add(category);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }

        return categories;
    }

    public boolean updateCategory(Category category) {
        if (category == null || category.getId() <= 0 || category.getType() == null || category.getType().trim().isEmpty()) {
            System.err.println("Invalid category update request!");
            return false;
        }

        String sql = "UPDATE category SET type = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getType().trim());
            stmt.setInt(2, category.getId());
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCategory(int categoryId) {
        if (categoryId <= 0) {
            System.err.println("Invalid category id for delete!");
            return false;
        }

        String sql = "DELETE FROM category WHERE id = ?";

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }
}
