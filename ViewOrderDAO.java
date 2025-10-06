package com.company.inventory3.dao;

import com.company.inventory3.database.DatabaseConnection;
import com.company.inventory3.model.ViewOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewOrderDAO {

    public boolean createOrder(ViewOrder order) {
        String sql = "INSERT INTO view_order (order_id, customer_id, customer_name, product_id, product_name, quantity_ordered, unit_price, total_price, created_by_employee_id, order_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, datetime('now'))";

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, order.getOrderId());
            stmt.setInt(2, order.getCustomerId());
            stmt.setString(3, order.getCustomerName());
            stmt.setInt(4, order.getProductId());
            stmt.setString(5, order.getProductName());
            stmt.setInt(6, order.getQuantityOrdered());
            stmt.setDouble(7, order.getUnitPrice());
            stmt.setDouble(8, order.getTotalPrice());
            stmt.setInt(9, order.getCreatedByEmployeeId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
            return false;
        }
    }

    public List<ViewOrder> getAllOrders() {
        List<ViewOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM view_order ORDER BY order_date DESC";

        try (Connection conn = DatabaseConnection.getDBConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
        }
        return orders;
    }

    public List<ViewOrder> getOrdersByCustomerId(int customerId) {
        List<ViewOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM view_order WHERE customer_id = ? ORDER BY order_date DESC";

        try (Connection conn = DatabaseConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching orders by customer: " + e.getMessage());
        }
        return orders;
    }

    private ViewOrder mapResultSetToOrder(ResultSet rs) throws SQLException {
        ViewOrder order = new ViewOrder();
        order.setId(rs.getInt("id"));
        order.setOrderId(rs.getInt("order_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setProductId(rs.getInt("product_id"));
        order.setProductName(rs.getString("product_name"));
        order.setQuantityOrdered(rs.getInt("quantity_ordered"));
        order.setUnitPrice(rs.getDouble("unit_price"));
        order.setTotalPrice(rs.getDouble("total_price"));

        Timestamp ts = rs.getTimestamp("order_date");
        if (ts != null) {
            order.setOrderDate(ts.toLocalDateTime());
        }

        order.setCreatedByEmployeeId(rs.getInt("created_by_employee_id"));
        return order;
    }
}
