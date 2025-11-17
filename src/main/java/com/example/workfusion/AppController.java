package com.example.workfusion;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Main application controller.
 * Manages the main dashboard scene and holds current user information.
 */
public class AppController {

    private static User currentUser;
    private boolean initialized = false;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label userLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private Button usersButton;

    @FXML
    private Button jobsButton;

    @FXML
    private Button seekersButton;

    @FXML
    private Button matchesButton;

    @FXML
    private Button itemsButton;

    /**
     * Initialize method called by JavaFX after FXML elements are injected.
     * Sets up event handlers and initial state.
     */
    @FXML
    public void initialize() {
        System.out.println("DEBUG: AppController.initialize() called");
        System.out.println("DEBUG: currentUser = " + currentUser);
        System.out.println("DEBUG: mainBorderPane = " + mainBorderPane);
        
        // Set up button event handlers
        logoutButton.setOnAction(event -> {
            System.out.println("DEBUG: Logout clicked");
            handleLogout();
        });
        usersButton.setOnAction(event -> {
            System.out.println("DEBUG: Users clicked");
            loadUsersView();
        });
        jobsButton.setOnAction(event -> {
            System.out.println("DEBUG: Jobs clicked");
            loadJobsView();
        });
        seekersButton.setOnAction(event -> {
            System.out.println("DEBUG: Seekers clicked");
            loadSeekersView();
        });
        matchesButton.setOnAction(event -> {
            System.out.println("DEBUG: Matches clicked");
            loadMatchesView();
        });
        itemsButton.setOnAction(event -> {
            System.out.println("DEBUG: Items clicked");
            loadItemsView();
        });
        
        // Mark as initialized
        initialized = true;
        System.out.println("DEBUG: AppController initialized");
        
        // Load initial view (jobs) only if user data is set
        if (currentUser != null) {
            System.out.println("DEBUG: Loading initial jobs view");
            loadJobsView();
        }
    }

    /**
     * Set user data and update UI elements.
     * 
     * @param user The current logged-in user
     */
    public void setUserData(User user) {
        System.out.println("DEBUG: setUserData called with user: " + user);
        currentUser = user;
        userLabel.setText("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
        
        // Load initial view now that user data is set
        if (initialized) {
            System.out.println("DEBUG: App is initialized, loading jobs view");
            loadJobsView();
        } else {
            System.out.println("DEBUG: App not yet initialized");
        }
    }

    /**
     * Get the current logged-in user.
     * 
     * @return The current user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Set the current logged-in user.
     * 
     * @param user The user to set as current
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Handle logout button click.
     * Clears current user and returns to login screen.
     */
    private void handleLogout() {
        try {
            // Clear current user
            currentUser = null;
            
            // Load login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/workfusion/login.fxml"));
            BorderPane root = loader.load();
            
            // Create and show login scene
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 400, 300);
            URL cssUrl = getClass().getResource("/com/example/workfusion/app-theme.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            
            // Get the current stage and switch scenes
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setTitle("Delphi Minds - Login");
            stage.setScene(scene);
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.show();
        } catch (IOException e) {
            showError("Error", "Failed to load login screen: " + e.getMessage());
        }
    }

    /**
     * Load the users view (admin-only functionality).
     */
    private void loadUsersView() {
        // Check if user has admin role
        if (currentUser == null || !"admin".equals(currentUser.getRole())) {
            if (currentUser != null) {
                showError("Access Denied", "Only administrators can manage users.");
            }
            return;
        }
        
        try {
            System.out.println("DEBUG: Loading users.fxml");
            URL fxmlUrl = getClass().getResource("/com/example/workfusion/users.fxml");
            System.out.println("DEBUG: FXML URL = " + fxmlUrl);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find users.fxml");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            VBox usersView = loader.load();
            System.out.println("DEBUG: users.fxml loaded successfully");
            
            // Get the controller and set the current user
            UsersController usersController = loader.getController();
            System.out.println("DEBUG: UsersController = " + usersController);
            usersController.setAppController(this);
            
            if (mainBorderPane != null) {
                mainBorderPane.setCenter(usersView);
            }
        } catch (IOException e) {
            showError("Error", "Failed to load users view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load the jobs view.
     */
    private void loadJobsView() {
        // Only proceed if currentUser and mainBorderPane are not null
        if (currentUser == null || mainBorderPane == null) {
            return;
        }
        
        try {
            System.out.println("DEBUG: Loading jobs.fxml");
            URL fxmlUrl = getClass().getResource("/com/example/workfusion/jobs.fxml");
            System.out.println("DEBUG: FXML URL = " + fxmlUrl);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find jobs.fxml");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            VBox jobsView = loader.load();
            System.out.println("DEBUG: jobs.fxml loaded successfully");
            
            // Get the controller and set the current user
            JobsController jobsController = loader.getController();
            System.out.println("DEBUG: JobsController = " + jobsController);
            jobsController.setAppController(this);
            
            mainBorderPane.setCenter(jobsView);
        } catch (IOException e) {
            showError("Error", "Failed to load jobs view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load the seekers view.
     */
    private void loadSeekersView() {
        // Only proceed if currentUser is not null
        if (currentUser == null) {
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/workfusion/seekers.fxml"));
            VBox seekersView = loader.load();
            
            // Get the controller and set the current user
            SeekersController seekersController = loader.getController();
            seekersController.setAppController(this);
            
            if (mainBorderPane != null) {
                mainBorderPane.setCenter(seekersView);
            }
        } catch (IOException e) {
            showError("Error", "Failed to load seekers view: " + e.getMessage());
        }
    }

    /**
     * Load the matches view.
     */
    private void loadMatchesView() {
        // Only proceed if currentUser is not null
        if (currentUser == null) {
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/workfusion/matches.fxml"));
            VBox matchesView = loader.load();
            
            // Get the controller and set the current user
            MatchesController matchesController = loader.getController();
            matchesController.setAppController(this);
            
            if (mainBorderPane != null) {
                mainBorderPane.setCenter(matchesView);
            }
        } catch (IOException e) {
            showError("Error", "Failed to load matches view: " + e.getMessage());
        }
    }

    /**
     * Load the items view.
     */
    private void loadItemsView() {
        // Only proceed if currentUser is not null
        if (currentUser == null) {
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/workfusion/items.fxml"));
            VBox itemsView = loader.load();
            
            // Get the controller and set the current user
            ItemsController itemsController = loader.getController();
            itemsController.setAppController(this);
            
            if (mainBorderPane != null) {
                mainBorderPane.setCenter(itemsView);
            }
        } catch (IOException e) {
            showError("Error", "Failed to load items view: " + e.getMessage());
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