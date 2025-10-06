package com.company.inventory3.controller;

import com.company.inventory3.service.AuthenticationService;
import com.company.inventory3.utils.AlertUtils;
import com.company.inventory3.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;

    private AuthenticationService authService;

    public void initialize() {
        authService = new AuthenticationService();
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both email and password");
            return;
        }

        if (authService.login(email, password)) {
            messageLabel.setText("Login successful!");
            openDashboard();
        } else {
            messageLabel.setText("Invalid credentials");
        }
    }

    private void openDashboard() {
        try {
            Stage currentStage = (Stage) loginButton.getScene().getWindow();


            String fxmlFile = SessionManager.isAdmin()
                    ? "/com/admin-dashboard.fxml"
                    : "/com/employee-dashboard.fxml";

            System.out.println("DEBUG: trying to load -> " + fxmlFile);
            URL fxmlUrl = getClass().getResource(fxmlFile);
            System.out.println("DEBUG: resolved URL -> " + fxmlUrl);

            if (fxmlUrl == null) {
                throw new IllegalStateException("FXML not found in classpath: " + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(loader.load(), 1000, 700);

            currentStage.setTitle("Inventory Management - Dashboard");
            currentStage.setScene(scene);
            currentStage.setMaximized(true);
            currentStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Error", "Failed to open dashboard: " + e.getMessage());
        }
    }

}
