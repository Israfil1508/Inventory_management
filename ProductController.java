package com.company.inventory3.controller;

import com.company.inventory3.dao.ProductDAO;
import com.company.inventory3.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, String> colCategory;

    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtCategoryId;

    private ProductDAO productDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productDAO = new ProductDAO();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        loadProducts();
    }

    private void loadProducts() {
        List<Product> products = productDAO.getAllProducts();
        ObservableList<Product> productList = FXCollections.observableArrayList(products);
        productTable.setItems(productList);
    }

    @FXML
    private void addProduct() {
        try {
            String name = txtName.getText();
            double price = Double.parseDouble(txtPrice.getText());
            int quantity = Integer.parseInt(txtQuantity.getText());
            int categoryId = Integer.parseInt(txtCategoryId.getText());
            Product product = new Product(name, price, quantity, categoryId);
            productDAO.addProduct(product);
            loadProducts();
            clearForm();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Invalid input!");
        }
    }

    @FXML
    private void updateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setName(txtName.getText());
                selected.setPrice(Double.parseDouble(txtPrice.getText()));
                selected.setQuantity(Integer.parseInt(txtQuantity.getText()));
                selected.setCategoryId(Integer.parseInt(txtCategoryId.getText()));
                productDAO.updateProduct(selected);
                loadProducts();
                clearForm();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Invalid input!");
            }
        } else {
            showAlert("Warning", "Please select a product to update.");
        }
    }

    @FXML
    private void deleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            productDAO.deleteProduct(selected.getId());
            loadProducts();
            clearForm();
        } else {
            showAlert("Warning", "Please select a product to delete.");
        }
    }

    private void clearForm() {
        txtName.clear();
        txtPrice.clear();
        txtQuantity.clear();
        txtCategoryId.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
