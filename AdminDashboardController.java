package com.company.inventory3.controller;

import com.company.inventory3.dao.*;
import com.company.inventory3.model.*;
import com.company.inventory3.utils.AlertUtils;
import com.company.inventory3.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

public class AdminDashboardController {

    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Integer> empIdColumn;
    @FXML private TableColumn<Employee, String> empNameColumn;
    @FXML private TableColumn<Employee, String> empEmailColumn;
    @FXML private TableColumn<Employee, String> empRoleColumn;
    @FXML private TableColumn<Employee, Void> empActionsColumn;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> prodIdColumn;
    @FXML private TableColumn<Product, String> prodNameColumn;
    @FXML private TableColumn<Product, Double> prodPriceColumn;
    @FXML private TableColumn<Product, Integer> prodQuantityColumn;
    @FXML private TableColumn<Product, String> prodCategoryColumn;
    @FXML private TableColumn<Product, Void> prodActionsColumn;



    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> custIdColumn;
    @FXML private TableColumn<Customer, String> custNameColumn;
    @FXML private TableColumn<Customer, String> custEmailColumn;
    @FXML private TableColumn<Customer, Double> custTotalColumn;
    @FXML private TableColumn<Customer, Void> custActionsColumn;

    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> catIdColumn;
    @FXML private TableColumn<Category, String> catTypeColumn;
    @FXML private TableColumn<Category, Void> catActionsColumn;

    @FXML private Label welcomeLabel;
    @FXML private Button logoutButton;

    private EmployeeDAO employeeDAO;
    private ProductDAO productDAO;
    private CustomerDAO customerDAO;
    private CategoryDAO categoryDAO;

    public void initialize() {
        employeeDAO = new EmployeeDAO();
        productDAO = new ProductDAO();
        customerDAO = new CustomerDAO();
        categoryDAO = new CategoryDAO();

        welcomeLabel.setText("Welcome, " + SessionManager.getCurrentEmployee().getName());

        setupEmployeeTable();
        setupProductTable();
        setupCustomerTable();
        setupCategoryTable();

        loadAllData();
    }



    private void setupEmployeeTable() {
        empIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        empNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        empEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        empRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        empActionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.setOnAction(e -> editEmployee(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> deleteEmployee(getTableRow().getItem()));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new javafx.scene.layout.HBox(5, editBtn, deleteBtn));
            }
        });
    }

    private void setupProductTable() {
        prodIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        prodNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        prodPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        prodQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        prodCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        prodActionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.setOnAction(e -> editProduct(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> deleteProduct(getTableRow().getItem()));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new javafx.scene.layout.HBox(5, editBtn, deleteBtn));
            }
        });
    }

    private void setupCustomerTable() {
        custIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        custNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        custEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        custTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        custActionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.setOnAction(e -> editCustomer(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> deleteCustomer(getTableRow().getItem()));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new javafx.scene.layout.HBox(5, editBtn, deleteBtn));
            }
        });
    }

    private void setupCategoryTable() {
        catIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        catTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        catActionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            {
                editBtn.setOnAction(e -> editCategory(getTableRow().getItem()));
                deleteBtn.setOnAction(e -> deleteCategory(getTableRow().getItem()));
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new javafx.scene.layout.HBox(5, editBtn, deleteBtn));
            }
        });
    }

    private void loadAllData() {
        refreshEmployees();
        refreshProducts();
        refreshCustomers();
        refreshCategories();
    }

    @FXML private void refreshEmployees() {
        employeeTable.setItems(FXCollections.observableArrayList(employeeDAO.getAllEmployees()));
    }

    @FXML private void refreshProducts() {
        productTable.setItems(FXCollections.observableArrayList(productDAO.getAllProducts()));
    }

    @FXML private void refreshCustomers() {
        customerTable.setItems(FXCollections.observableArrayList(customerDAO.getAllCustomers()));
    }

    @FXML private void refreshCategories() {
        categoryTable.setItems(FXCollections.observableArrayList(categoryDAO.getAllCategories()));
    }

    @FXML private void showAddEmployee() { showEmployeeDialog(null); }
    @FXML private void showAddProduct() { showProductDialog(null); }
    @FXML private void showAddCustomer() { showCustomerDialog(null); }
    @FXML private void showAddCategory() { showCategoryDialog(null); }

    private void editEmployee(Employee employee) { if (employee != null) showEmployeeDialog(employee); }
    private void editProduct(Product product) { if (product != null) showProductDialog(product); }
    private void editCustomer(Customer customer) { if (customer != null) showCustomerDialog(customer); }
    private void editCategory(Category category) { if (category != null) showCategoryDialog(category); }

    private void deleteEmployee(Employee employee) {
        if (employee != null && AlertUtils.showConfirmation("Confirm Delete","Are you sure you want to delete this employee?")) {
            if (employeeDAO.deleteEmployee(employee.getId())) {
                AlertUtils.showInfo("Success", "Employee deleted successfully");
                refreshEmployees();
            } else AlertUtils.showError("Error", "Failed to delete employee");
        }
    }

    private void deleteProduct(Product product) {
        if (product != null && AlertUtils.showConfirmation("Confirm Delete","Are you sure you want to delete this product?")) {
            if (productDAO.deleteProduct(product.getId())) {
                AlertUtils.showInfo("Success", "Product deleted successfully");
                refreshProducts();
            } else AlertUtils.showError("Error", "Failed to delete product");
        }
    }

    private void deleteCustomer(Customer customer) {
        if (customer != null && AlertUtils.showConfirmation("Confirm Delete","Are you sure you want to delete this customer?")) {
            if (customerDAO.deleteCustomer(customer.getId())) {
                AlertUtils.showInfo("Success", "Customer deleted successfully");
                refreshCustomers();
            } else AlertUtils.showError("Error", "Failed to delete customer");
        }
    }

    private void deleteCategory(Category category) {
        if (category != null && AlertUtils.showConfirmation("Confirm Delete","Are you sure you want to delete this category?")) {
            if (categoryDAO.deleteCategory(category.getId())) {
                AlertUtils.showInfo("Success", "Category deleted successfully");
                refreshCategories();
            } else AlertUtils.showError("Error", "Failed to delete category");
        }
    }

    private void showEmployeeDialog(Employee employee) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle(employee == null ? "Add Employee" : "Edit Employee");
        dialog.setHeaderText(null);
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20,150,10,10));

        TextField nameField = new TextField(employee != null ? employee.getName() : "");
        TextField emailField = new TextField(employee != null ? employee.getEmail() : "");
        TextArea addressField = new TextArea(employee != null ? employee.getAddress() : "");
        PasswordField passwordField = new PasswordField();
        ComboBox<Employee.Role> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll(Employee.Role.values());
        roleCombo.setValue(employee != null ? employee.getRole() : Employee.Role.EMPLOYEE);
        addressField.setPrefRowCount(3);

        grid.addRow(0,new Label("Name:"),nameField);
        grid.addRow(1,new Label("Email:"),emailField);
        grid.addRow(2,new Label("Address:"),addressField);
        if (employee == null) { grid.addRow(3,new Label("Password:"),passwordField); }
        grid.addRow(4,new Label("Role:"),roleCombo);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
                    AlertUtils.showError("Validation Error","Name and email are required");
                    return null;
                }
                if (employee == null && passwordField.getText().trim().isEmpty()) {
                    AlertUtils.showError("Validation Error","Password is required for new employees");
                    return null;
                }
                Employee emp = employee != null ? employee : new Employee();
                emp.setName(nameField.getText().trim());
                emp.setEmail(emailField.getText().trim());
                emp.setAddress(addressField.getText().trim());
                emp.setRole(roleCombo.getValue());
                if (employee == null) emp.setPassword(passwordField.getText());
                return emp;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(emp -> {
            boolean success = (employee == null) ? employeeDAO.updateEmployee(emp) : employeeDAO.updateEmployee(emp);
            if (success) {
                AlertUtils.showInfo("Success", employee == null ? "Employee created successfully" : "Employee updated successfully");
                refreshEmployees();
            } else AlertUtils.showError("Error", "Failed to save employee");
        });
    }

    private void showProductDialog(Product product) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(product == null ? "Add Product" : "Edit Product");
        dialog.setHeaderText(null);
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20,150,10,10));

        TextField nameField = new TextField(product != null ? product.getName() : "");
        TextField priceField = new TextField(product != null ? String.valueOf(product.getPrice()) : "");
        TextField quantityField = new TextField(product != null ? String.valueOf(product.getQuantity()) : "");
        ComboBox<Category> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(categoryDAO.getAllCategories());
        if (product != null) {
            categoryCombo.getItems().stream().filter(c -> c.getId() == product.getCategoryId()).findFirst().ifPresent(categoryCombo::setValue);
        }

        grid.addRow(0,new Label("Name:"),nameField);
        grid.addRow(1,new Label("Price:"),priceField);
        grid.addRow(2,new Label("Quantity:"),quantityField);
        grid.addRow(3,new Label("Category:"),categoryCombo);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                try {
                    if (nameField.getText().trim().isEmpty() || categoryCombo.getValue() == null) {
                        AlertUtils.showError("Validation Error","Name and category are required");
                        return null;
                    }
                    Product prod = product != null ? product : new Product(  );
                    prod.setName(nameField.getText().trim());
                    prod.setPrice(Double.parseDouble(priceField.getText().trim()));
                    prod.setQuantity(Integer.parseInt(quantityField.getText().trim()));
                    prod.setCategoryId(categoryCombo.getValue().getId());
                    return prod;
                } catch (NumberFormatException e) {
                    AlertUtils.showError("Validation Error","Price and quantity must be numbers");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(prod -> {
            boolean success = (product == null) ? productDAO.addProduct(prod) : productDAO.updateProduct(prod);
            if (success) {
                AlertUtils.showInfo("Success", product == null ? "Product created successfully" : "Product updated successfully");
                refreshProducts();
            } else AlertUtils.showError("Error", "Failed to save product");
        });
    }

    private void showCustomerDialog(Customer customer) {
        Dialog<Customer> dialog = new Dialog<>();
        dialog.setTitle(customer == null ? "Add Customer" : "Edit Customer");
        dialog.setHeaderText(null);
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20,150,10,10));

        TextField nameField = new TextField(customer != null ? customer.getName() : "");
        TextField emailField = new TextField(customer != null ? customer.getEmail() : "");

        grid.addRow(0,new Label("Name:"),nameField);
        grid.addRow(1,new Label("Email:"),emailField);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
                    AlertUtils.showError("Validation Error","Name and email are required");
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
            boolean success = (customer == null) ? customerDAO.updateCustomer(cust) : customerDAO.updateCustomer(cust);
            if (success) {
                AlertUtils.showInfo("Success", customer == null ? "Customer created successfully" : "Customer updated successfully");
                refreshCustomers();
            } else AlertUtils.showError("Error", "Failed to save customer");
        });
    }

    private void showCategoryDialog(Category category) {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle(category == null ? "Add Category" : "Edit Category");
        dialog.setHeaderText(null);
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20,150,10,10));

        TextField typeField = new TextField(category != null ? category.getType() : "");
        grid.addRow(0,new Label("Type:"),typeField);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                if (typeField.getText().trim().isEmpty()) {
                    AlertUtils.showError("Validation Error","Category type is required");
                    return null;
                }
                Category cat = category != null ? category : new Category();
                cat.setType(typeField.getText().trim());
                return cat;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(cat -> {
            boolean success = (category == null) ? categoryDAO.updateCategory(cat) : categoryDAO.updateCategory(cat);
            if (success) {
                AlertUtils.showInfo("Success", category == null ? "Category created successfully" : "Category updated successfully");
                refreshCategories();
            } else AlertUtils.showError("Error", "Failed to save category");
        });
    }

    @FXML private void handleLogout() {
        if (AlertUtils.showConfirmation("Confirm Logout","Are you sure you want to logout?")) {
            try {
                SessionManager.logout();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/login.fxml"));
                Scene scene = new Scene(loader.load(), 400, 300);
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.setTitle("Inventory Management System - Login");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.centerOnScreen();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtils.showError("Error","Failed to logout. Please try again.");
            }
        }
    }
}
