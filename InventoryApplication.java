package com.company.inventory3;

import com.company.inventory3.database.DatabaseConnection;
import com.company.inventory3.database.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class InventoryApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);

            primaryStage.setTitle("Inventory Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        DatabaseConnection.closeConnection();
        super.stop();
    }

    public static void main(String[] args) {

        try {
            DatabaseInitializer.createDatabaseAndTables();

            launch(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
