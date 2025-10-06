package com.company.inventory3.controller;

import com.company.inventory3.dao.*;
import com.company.inventory3.model.*;
import com.company.inventory3.service.OrderService;
import com.company.inventory3.service.PDFService;
import com.company.inventory3.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OrderController {

    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;
    @FXML private TableView<OrderItemDisplay> orderItemsTable;
    @FXML private TableColumn<OrderItemDisplay, String> orderProdNameColumn;
    @FXML private TableColumn<OrderItemDisplay, Integer> orderQuantityColumn;
    @FXML private TableColumn<OrderItemDisplay, Double> orderUnitPriceColumn;
    @FXML private TableColumn<OrderItemDisplay, Double> orderTotalColumn;
    @FXML private TableColumn<OrderItemDisplay, Void> orderRemoveColumn;
    @FXML private Label grandTotalLabel;
    @FXML private Button processOrderButton;

    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private OrderService orderService;
    private PDFService pdfService;
    private ObservableList<OrderItemDisplay> orderItems;
    private double grandTotal = 0.0;

    public void initialize() {
        customerDAO = new CustomerDAO();
        productDAO = new ProductDAO();
        orderService = new OrderService();
        pdfService = new PDFService();
        orderItems = FXCollections.observableArrayList();
        setupOrderTable();
        loadCustomers();
        loadProducts();
        orderItemsTable.setItems(orderItems);
    }

    private void setupOrderTable() {
        orderProdNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        orderQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        orderUnitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        orderRemoveColumn.setCellFactory(col -> new TableCell<>() {
            private final Button removeBtn = new Button("Remove");
            {
                removeBtn.setOnAction(e -> removeOrderItem(getTableRow().getItem()));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });
    }

    private void loadCustomers() {
        customerComboBox.getItems().clear();
        customerComboBox.getItems().addAll(customerDAO.getAllCustomers());
    }

    private void loadProducts() {
        productComboBox.getItems().clear();
        productComboBox.getItems().addAll(productDAO.getAllProducts());
    }

    @FXML
    private void addProductToOrder() {
        Product selectedProduct = productComboBox.getValue();
        String quantityText = quantityField.getText().trim();
        if (selectedProduct == null) {
            AlertUtils.showWarning("Validation Error", "Please select a product");
            return;
        }
        if (quantityText.isEmpty()) {
            AlertUtils.showWarning("Validation Error", "Please enter quantity");
            return;
        }
        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                AlertUtils.showWarning("Validation Error", "Quantity must be greater than 0");
                return;
            }
            if (quantity > selectedProduct.getQuantity()) {
                AlertUtils.showWarning("Stock Error", "Not enough stock available. Available: " + selectedProduct.getQuantity());
                return;
            }
            OrderItemDisplay existingItem = orderItems.stream()
                    .filter(item -> item.getProductId() == selectedProduct.getId())
                    .findFirst()
                    .orElse(null);
            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                if (newQuantity > selectedProduct.getQuantity()) {
                    AlertUtils.showWarning("Stock Error", "Total quantity exceeds available stock");
                    return;
                }
                existingItem.setQuantity(newQuantity);
                existingItem.updateTotal();
            } else {
                orderItems.add(new OrderItemDisplay(
                        selectedProduct.getId(),
                        selectedProduct.getName(),
                        quantity,
                        selectedProduct.getPrice()
                ));
            }
            updateGrandTotal();
            quantityField.clear();
            productComboBox.setValue(null);
        } catch (NumberFormatException e) {
            AlertUtils.showWarning("Validation Error", "Please enter a valid quantity");
        }
    }

    private void removeOrderItem(OrderItemDisplay item) {
        if (item != null) {
            orderItems.remove(item);
            updateGrandTotal();
        }
    }

    private void updateGrandTotal() {
        grandTotal = orderItems.stream().mapToDouble(OrderItemDisplay::getTotal).sum();
        grandTotalLabel.setText("Grand Total: $" + String.format("%.2f", grandTotal));
    }

    @FXML
    private void clearOrder() {
        if (AlertUtils.showConfirmation("Clear Order", "Are you sure you want to clear all items?")) {
            orderItems.clear();
            updateGrandTotal();
        }
    }

    @FXML
    private void processOrder() {
        Customer selectedCustomer = customerComboBox.getValue();
        if (selectedCustomer == null) {
            AlertUtils.showWarning("Validation Error", "Please select a customer");
            return;
        }
        if (orderItems.isEmpty()) {
            AlertUtils.showWarning("Validation Error", "Please add items to the order");
            return;
        }
        List<OrderService.OrderItem> serviceOrderItems = new ArrayList<>();
        for (OrderItemDisplay item : orderItems) {
            serviceOrderItems.add(new OrderService.OrderItem(item.getProductId(), item.getQuantity()));
        }
        if (orderService.createOrder(serviceOrderItems, selectedCustomer)) {
            AlertUtils.showInfo("Success", "Order processed successfully!");
            if (AlertUtils.showConfirmation("Generate Invoice", "Would you like to generate a PDF invoice?")) {
                generateInvoice(serviceOrderItems, selectedCustomer);
            }
            orderItems.clear();
            updateGrandTotal();
            customerComboBox.setValue(null);
            loadProducts();
        } else {
            AlertUtils.showError("Error", "Failed to process order");
        }
    }

    private void generateInvoice(List<OrderService.OrderItem> serviceOrderItems, Customer customer) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Invoice");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("Invoice_" + customer.getName().replaceAll(" ", "_") + "_" +
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
        File file = fileChooser.showSaveDialog(processOrderButton.getScene().getWindow());
        if (file != null) {
            if (pdfService.generateInvoice(serviceOrderItems, customer, file.getAbsolutePath())) {
                AlertUtils.showInfo("Success", "Invoice generated successfully at: " + file.getAbsolutePath());
            } else {
                AlertUtils.showError("Error", "Failed to generate invoice");
            }
        }
    }

    @FXML
    private void showAddCustomerDialog() {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle("Add New Customer");
        dialog.setHeaderText(null);
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
                    AlertUtils.showError("Validation Error", "Name and email are required");
                    return null;
                }
                Customer customer = new Customer();
                customer.setName(nameField.getText().trim());
                customer.setEmail(emailField.getText().trim());
                return customer;
            }
            return null;
        });
        dialog.showAndWait().ifPresent(customer -> {
            if (customerDAO.createCustomer(customer)) {
                AlertUtils.showInfo("Success", "Customer created successfully");
                loadCustomers();
                customerComboBox.getItems().stream()
                        .filter(c -> c.getEmail().equals(customer.getEmail()))
                        .findFirst()
                        .ifPresent(customerComboBox::setValue);
            } else {
                AlertUtils.showError("Error", "Failed to create customer");
            }
        });
    }

    public static class OrderItemDisplay {
        private int productId;
        private String productName;
        private int quantity;
        private double unitPrice;
        private double total;

        public OrderItemDisplay(int productId, String productName, int quantity, double unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.total = quantity * unitPrice;
        }

        public void updateTotal() {
            this.total = this.quantity * this.unitPrice;
        }

        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
            updateTotal();
        }
        public double getUnitPrice() { return unitPrice; }
        public double getTotal() { return total; }
    }
}
