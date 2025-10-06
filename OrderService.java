package com.company.inventory3.service;

import com.company.inventory3.dao.CustomerDAO;
import com.company.inventory3.dao.OrderDAO;
import com.company.inventory3.dao.ProductDAO;
import com.company.inventory3.model.Customer;
import com.company.inventory3.model.Order;
import com.company.inventory3.model.Product;
import com.company.inventory3.utils.SessionManager;

import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final CustomerDAO customerDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
        this.customerDAO = new CustomerDAO();
    }

    public static class OrderItem {
        private int productId;
        private int quantity;

        public OrderItem(int productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public int getProductId() { return productId; }
        public int getQuantity() { return quantity; }
    }


    public boolean createOrder(List<OrderItem> items, Customer customer) {
        try {
            double totalOrderAmount = 0.0;

            for (OrderItem item : items) {
                Product product = productDAO.getProductById(item.getProductId());
                if (product == null) return false;

                if (item.getQuantity() > product.getQuantity()) {
                    return false;
                }

                double total = product.getPrice() * item.getQuantity();
                totalOrderAmount += total;

                Order order = new Order();
                order.setCustomerId(customer.getId());
                order.setCustomerName(customer.getName());
                order.setProductId(product.getId());
                order.setProductName(product.getName());
                order.setQuantityOrdered(item.getQuantity());
                order.setUnitPrice(product.getPrice());
                order.setTotalPrice(total);

                int employeeId = SessionManager.getCurrentEmployee() != null
                        ? SessionManager.getCurrentEmployee().getId()
                        : 0;
                order.setCreatedByEmployeeId(employeeId);

                if (!orderDAO.insertOrder(order)) {
                    return false;
                }


                productDAO.updateProductQuantity(product.getId(), product.getQuantity() - item.getQuantity());
            }


            customerDAO.updateCustomerTotalCost(customer.getId(), totalOrderAmount);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
