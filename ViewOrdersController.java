package com.company.inventory3.controller;

import com.company.inventory3.dao.ViewOrderDAO;
import com.company.inventory3.model.ViewOrder;
import com.company.inventory3.utils.AlertUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewOrdersController {

    @FXML private TableView<ViewOrder> ordersTable;
    @FXML private TableColumn<ViewOrder, Integer> orderIdColumn;
    @FXML private TableColumn<ViewOrder, String> orderDateColumn;
    @FXML private TableColumn<ViewOrder, String> orderCustomerColumn;
    @FXML private TableColumn<ViewOrder, String> orderProductColumn;
    @FXML private TableColumn<ViewOrder, Integer> orderQuantityColumn;
    @FXML private TableColumn<ViewOrder, Double> orderUnitPriceColumn;
    @FXML private TableColumn<ViewOrder, Double> orderTotalColumn;

    private ViewOrderDAO viewOrderDAO;

    public void initialize() {
        viewOrderDAO = new ViewOrderDAO();
        setupOrdersTable();
        refreshOrders();
    }

    private void setupOrdersTable() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
        );
        orderCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        orderProductColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        orderQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantityOrdered"));
        orderUnitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        orderTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }

    @FXML
    private void refreshOrders() {
        List<ViewOrder> orderList = viewOrderDAO.getAllOrders();
        ObservableList<ViewOrder> orders = FXCollections.observableArrayList(orderList);
        ordersTable.setItems(orders);
    }

    @FXML
    private void exportOrders() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Orders");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("Orders_Export_" +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");

        File file = fileChooser.showSaveDialog(ordersTable.getScene().getWindow());
        if (file != null) {
            exportToCSV(file);
        }
    }

    private void exportToCSV(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Order ID,Date,Customer,Product,Quantity,Unit Price,Total\n");

            for (ViewOrder order : ordersTable.getItems()) {
                writer.write(String.format("%d,\"%s\",\"%s\",\"%s\",%d,%.2f,%.2f\n",
                        order.getId(),
                        order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        order.getCustomerName(),
                        order.getProductName(),
                        order.getQuantityOrdered(),
                        order.getUnitPrice(),
                        order.getTotalPrice()
                ));
            }

            AlertUtils.showInfo("Success", "Orders exported successfully to: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Error", "Failed to export orders");
        }
    }
}
