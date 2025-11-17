package com.example.workfusion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.example.workfusion.dao.UserDao;

/**
 * Controller for the users management view (admin-only).
 * Handles UI interactions and CRUD operations for users.
 */
public class UsersController {

    private AppController appController;
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private final UserDao userDao = new UserDao();

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> createdAtColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button resetPasswordButton;

    /**
     * Initialize method called by JavaFX after FXML elements are injected.
     * Sets up the table and loads initial data.
     */
    @FXML
    public void initialize() {
        setupTable();
        loadUsers();
        
        // Set up role combo box
        roleComboBox.getItems().addAll("admin", "employer", "seeker");
        roleComboBox.setValue("seeker");
        
        // Set up button event handlers
        addButton.setOnAction(event -> handleAddUser());
        updateButton.setOnAction(event -> handleUpdateUser());
        deleteButton.setOnAction(event -> handleDeleteUser());
        clearButton.setOnAction(event -> clearForm());
        resetPasswordButton.setOnAction(event -> handleResetPassword());
        
        // Disable update/delete buttons initially
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetPasswordButton.setDisable(true);
        
        // Enable update/delete when a user is selected
        usersTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                updateButton.setDisable(newValue == null);
                deleteButton.setDisable(newValue == null);
                resetPasswordButton.setDisable(newValue == null);
                if (newValue != null) {
                    populateForm(newValue);
                }
            }
        );
    }

    /**
     * Set the reference to the main application controller.
     * 
     * @param appController The main application controller
     */
    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    /**
     * Set up the users table with columns and bindings.
     */
    private void setupTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        usersTable.setItems(userList);
    }

    /**
     * Load users from the database into the table.
     */
    public void loadUsers() {
        javafx.concurrent.Task<List<User>> task = new javafx.concurrent.Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return userDao.readAll();
            }
        };
        
        task.setOnSucceeded(e -> {
            userList.setAll(task.getValue());
            System.out.println("DEBUG: Loaded " + userList.size() + " users from DB");
        });
        
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            showError("Failed to load users", "Failed to load users: " + task.getException().getMessage());
        });
        
        new Thread(task, "users-reader").start();
    }

    /**
     * Handle add user button click.
     */
    private void handleAddUser() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty() || role == null) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }
        
        // Check if username is already taken
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                showError("Validation Error", "Username already exists.");
                return;
            }
        }
        
        javafx.concurrent.Task<User> task = new javafx.concurrent.Task<User>() {
            @Override
            protected User call() throws Exception {
                String passwordHash = Database.hashPassword(password);
                User user = new User(username, passwordHash, role);
                return userDao.create(user);
            }
        };
        
        task.setOnSucceeded(e -> {
            userList.add(task.getValue());
            clearForm();
            showInfo("Success", "User added successfully.");
            System.out.println("DEBUG: User added successfully");
        });
        
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            showError("Failed to add user", "Failed to add user: " + task.getException().getMessage());
        });
        
        new Thread(task, "user-creator").start();
    }

    /**
     * Handle update user button click.
     */
    private void handleUpdateUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("Selection Error", "Please select a user to update.");
            return;
        }
        
        String username = usernameField.getText().trim();
        String role = roleComboBox.getValue();
        
        // Validate input
        if (username.isEmpty() || role == null) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }
        
        // Check if username is already taken by another user
        for (User user : userList) {
            if (user.getUsername().equals(username) && user.getId() != selectedUser.getId()) {
                showError("Validation Error", "Username already exists.");
                return;
            }
        }
        
        User userToUpdate = new User(selectedUser.getId(), username, selectedUser.getPasswordHash(), role, selectedUser.getCreatedAt());
        
        javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return userDao.update(userToUpdate);
            }
        };
        
        task.setOnSucceeded(e -> {
            int selectedIndex = usersTable.getSelectionModel().getSelectedIndex();
            userList.set(selectedIndex, userToUpdate);
            showInfo("Success", "User updated successfully.");
            System.out.println("DEBUG: User updated successfully");
        });
        
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            showError("Failed to update user", "Failed to update user: " + task.getException().getMessage());
        });
        
        new Thread(task, "user-updater").start();
    }

    /**
     * Handle delete user button click.
     */
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("Selection Error", "Please select a user to delete.");
            return;
        }
        
        // Prevent deleting the current user
        if (selectedUser.getId() == AppController.getCurrentUser().getId()) {
            showError("Operation Not Allowed", "You cannot delete your own account.");
            return;
        }
        
        // Confirm deletion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the user '" + selectedUser.getUsername() + "'?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return userDao.delete(selectedUser.getId());
                }
            };
            
            task.setOnSucceeded(e -> {
                userList.remove(selectedUser);
                clearForm();
                showInfo("Success", "User deleted successfully.");
                System.out.println("DEBUG: User deleted successfully");
            });
            
            task.setOnFailed(e -> {
                task.getException().printStackTrace();
                showError("Failed to delete user", "Failed to delete user: " + task.getException().getMessage());
            });
            
            new Thread(task, "user-deleter").start();
        }
    }

    /**
     * Handle reset password button click.
     */
    private void handleResetPassword() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("Selection Error", "Please select a user to reset password.");
            return;
        }
        
        String newPassword = passwordField.getText();
        if (newPassword.isEmpty()) {
            showError("Validation Error", "Please enter a new password.");
            return;
        }
        
        javafx.concurrent.Task<Boolean> task = new javafx.concurrent.Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                String passwordHash = Database.hashPassword(newPassword);
                selectedUser.setPasswordHash(passwordHash);
                return userDao.update(selectedUser);
            }
        };
        
        task.setOnSucceeded(e -> {
            showInfo("Success", "Password reset successfully.");
            System.out.println("DEBUG: Password reset successfully");
        });
        
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            showError("Failed to reset password", "Failed to reset password: " + task.getException().getMessage());
        });
        
        new Thread(task, "password-resetter").start();
    }

    /**
     * Populate the form with data from a user.
     * 
     * @param user The user to populate the form with
     */
    private void populateForm(User user) {
        usernameField.setText(user.getUsername());
        passwordField.clear(); // Don't populate password field for security
        roleComboBox.setValue(user.getRole());
    }

    /**
     * Clear the form fields.
     */
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue("seeker");
        usersTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        resetPasswordButton.setDisable(true);
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

    /**
     * Show an information dialog to the user.
     * 
     * @param title The title of the information dialog
     * @param message The information message to display
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}