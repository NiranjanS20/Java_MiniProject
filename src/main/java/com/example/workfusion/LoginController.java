package com.example.workfusion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;

/**
 * Controller for the login scene.
 * Handles user authentication and session management.
 */
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    /**
     * Initialize method called by JavaFX after FXML elements are injected.
     * Sets up event handlers.
     */
    @FXML
    public void initialize() {
        // Set up login button event handler
        loginButton.setOnAction(event -> handleLogin());
        
        // Allow login with Enter key
        usernameField.setOnAction(event -> handleLogin());
        passwordField.setOnAction(event -> handleLogin());
    }

    /**
     * Handle login button click.
     * Validates credentials and transitions to main application scene on success.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Validation Error", "Please enter both username and password.");
            return;
        }

        try {
            // Authenticate user
            User user = authenticateUser(username, password);
            
            if (user != null) {
                // Login successful - set current user and switch to main scene
                AppController.setCurrentUser(user);
                switchToMainScene();
            } else {
                // Login failed
                showError("Login Failed", "Invalid username or password.");
            }
        } catch (Exception e) {
            showError("Database Error", "An error occurred while trying to log in: " + e.getMessage());
        }
    }

    /**
     * Authenticate user credentials against the database.
     * 
     * @param username The username to authenticate
     * @param password The password to verify
     * @return The User object if authentication is successful, null otherwise
     * @throws SQLException if there's a database error
     */
    private User authenticateUser(String username, String password) throws SQLException {
        User user = Database.getUserByUsername(username);
        
        if (user != null && Database.verifyPassword(password, user.getPasswordHash())) {
            return user;
        }
        
        return null;
    }

    /**
     * Switch to the main application scene after successful login.
     */
    private void switchToMainScene() {
        try {
            // Load the main application scene
            System.out.println("Attempting to load main.fxml");
            URL fxmlUrl = getClass().getResource("/com/example/workfusion/main.fxml");
            System.out.println("FXML URL: " + fxmlUrl);
            
            if (fxmlUrl == null) {
                throw new RuntimeException("Could not find main.fxml file");
            }
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(fxmlUrl);
            Parent root = loader.load();
            
            System.out.println("FXML loaded successfully");
            
            // Get the controller and set the current user
            AppController controller = loader.getController();
            controller.setUserData(AppController.getCurrentUser());
            
            // Create and show the main scene
            Scene scene = new Scene(root, 800, 600);
            URL cssUrl = getClass().getResource("/com/example/workfusion/app-theme.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            
            // Get the current stage and switch scenes
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Delphi Minds - Job Skills Matching");
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();
        } catch (Exception e) {
            showError("Error", "Failed to load main application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show an error dialog to the user.
     * 
     * @param title The title of the error dialog
     * @param message The error message to display
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}