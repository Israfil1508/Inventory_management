package com.company.inventory3.model;

import java.time.LocalDateTime;

public class ViewOrder {
    private int id;
    private int orderId;
    private int customerId;
    private String customerName;
    private int productId;
    private String productName;
    private int quantityOrdered;
    private double unitPrice;
    private double totalPrice;
    private LocalDateTime orderDate;
    private int createdByEmployeeId;

    public ViewOrder() {}

    public ViewOrder(int orderId, int customerId, String customerName,
                     int productId, String productName,
                     int quantityOrdered, double unitPrice, int createdByEmployeeId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.productId = productId;
        this.productName = productName;
        this.quantityOrdered = quantityOrdered;
        this.unitPrice = unitPrice;
        this.totalPrice = quantityOrdered * unitPrice;
        this.createdByEmployeeId = createdByEmployeeId;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantityOrdered() { return quantityOrdered; }
    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
        this.totalPrice = this.quantityOrdered * this.unitPrice;
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.totalPrice = this.quantityOrdered * this.unitPrice;
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public int getCreatedByEmployeeId() { return createdByEmployeeId; }
    public void setCreatedByEmployeeId(int createdByEmployeeId) { this.createdByEmployeeId = createdByEmployeeId; }
}
