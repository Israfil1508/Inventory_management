package com.company.inventory3.dao;

import com.company.inventory3.database.DatabaseConnection;
import com.company.inventory3.model.Order;
import com.company.inventory3.model.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public boolean insertOrder(Order order) {
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(order.getProductId());
        if (product == null) {
            System.err.println("Product not found!");
            return false;
        }
        if (product.getQuantity() < order.getQuantityOrdered()) {
            System.err.println("Not enough stock for product: " + product.getName());
            return false;
        }
        String sql = "INSERT INTO view_order (customer_id, customer_name, product_id, product_name, quantity_ordered, unit_price, total_price, order_date, created_by_employee_id) VALUES (?, ?, ?, ?, ?, ?, ?, datetime('now'), ?)";
        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, order.getCustomerId());
            stmt.setString(2, order.getCustomerName());
            stmt.setInt(3, order.getProductId());
            stmt.setString(4, order.getProductName());
            stmt.setInt(5, order.getQuantityOrdered());
            stmt.setDouble(6, order.getUnitPrice());
            stmt.setDouble(7, order.getTotalPrice());
            stmt.setInt(8, order.getCreatedByEmployeeId());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                int newQty = product.getQuantity() - order.getQuantityOrdered();
                productDAO.updateProductQuantity(product.getId(), newQty);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error inserting order: " + e.getMessage());
        }
        return false;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM view_order ORDER BY order_date DESC";
        try (Connection conn = DatabaseConnection.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setProductId(rs.getInt("product_id"));
                order.setProductName(rs.getString("product_name"));
                order.setQuantityOrdered(rs.getInt("quantity_ordered"));
                order.setUnitPrice(rs.getDouble("unit_price"));
                order.setTotalPrice(rs.getDouble("total_price"));
                String dateStr = rs.getString("order_date");
                order.setOrderDate(LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                order.setCreatedByEmployeeId(rs.getInt("created_by_employee_id"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
        }
        return orders;
    }
}
