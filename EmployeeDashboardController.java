package com.company.inventory3.controller;

import com.company.inventory3.dao.CustomerDAO;
import com.company.inventory3.model.Customer;
import com.company.inventory3.utils.AlertUtils;
import com.company.inventory3.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class EmployeeDashboardController {

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> custIdColumn;
    @FXML private TableColumn<Customer, String> custNameColumn;
    @FXML private TableColumn<Customer, String> custEmailColumn;
    @FXML private TableColumn<Customer, Double> custTotalColumn;
    @FXML private TableColumn<Customer, Void> custActionsColumn;

    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;

    private CustomerDAO customerDAO;

    public void initialize() {
        customerDAO = new CustomerDAO();

        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentEmployee().getName());

        setupCustomerTable();
        refreshCustomers();
    }

    private void setupCustomerTable() {
        custIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        custNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        custEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        custTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        custActionsColumn.setCellFactory(col -> {
            return new TableCell<Customer, Void>() {
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Delete");

                {
                    editBtn.setOnAction(e -> editCustomer(getTableRow().getItem()));
                    deleteBtn.setOnAction(e -> deleteCustomer(getTableRow().getItem()));
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(new javafx.scene.layout.HBox(5, editBtn, deleteBtn));
                    }
                }
            };
        });
    }

    @FXML
    private void refreshCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList((java.util.Collection<? extends Customer>) customerDAO.getAllCustomers());
        customerTable.setItems(customers);
    }

    @FXML
    private void showAddCustomer() {
        showCustomerDialog(null);
    }

    private void editCustomer(Customer customer) {
        if (customer != null) {
            showCustomerDialog(customer);
        }
    }

    private void deleteCustomer(Customer customer) {
        if (customer != null && AlertUtils.showConfirmation("Confirm Delete",
                "Are you sure you want to delete this customer?")) {
            if (customerDAO.deleteCustomer(customer.getId())) {
                AlertUtils.showInfo("Success", "Customer deleted successfully");
                refreshCustomers();
            } else {
                AlertUtils.showError("Error", "Failed to delete customer");
            }
        }
    }

    private void showCustomerDialog(Customer customer) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle(customer == null ? "Add Customer" : "Edit Customer");
        dialog.setHeaderText(null);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(customer != null ? customer.getName() : "");
        TextField emailField = new TextField(customer != null ? customer.getEmail() : "");

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

                Customer cust = customer != null ? customer : new Customer();
                cust.setName(nameField.getText().trim());
                cust.setEmail(emailField.getText().trim());

                return cust;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(cust -> {
            boolean success;
            if (customer == null) {
                success = customerDAO.createCustomer(cust);
            } else {
                success = customerDAO.updateCustomer(cust);
            }

            if (success) {
                AlertUtils.showInfo("Success", customer == null ? "Customer created successfully" : "Customer updated successfully");
                refreshCustomers();
            } else {
                AlertUtils.showError("Error", "Failed to save customer");
            }
        });
    }

    @FXML
    private void handleLogout() {
        if (AlertUtils.showConfirmation("Confirm Logout", "Are you sure you want to logout?")) {
            try {
                SessionManager.logout();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                Scene scene = new Scene(loader.load(), 400, 300);
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.setTitle("Inventory Management System");
                stage.setScene(scene);
                stage.setMaximized(false);
                stage.setResizable(false);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showError("Error", "Failed to logout");
            }
        }
    }
}