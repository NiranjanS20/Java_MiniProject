package com.example.workfusion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Controller for the seekers management view.
 * Handles UI interactions and CRUD operations for seekers.
 */
public class SeekersController {

    private AppController appController;
    private final ObservableList<Seeker> seekerList = FXCollections.observableArrayList();

    @FXML
    private TableView<Seeker> seekersTable;

    @FXML
    private TableColumn<Seeker, String> nameColumn;

    @FXML
    private TableColumn<Seeker, String> emailColumn;

    @FXML
    private TableColumn<Seeker, String> skillsColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextArea skillsArea;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    /**
     * Initialize method called by JavaFX after FXML elements are injected.
     * Sets up the table and loads initial data.
     */
    @FXML
    public void initialize() {
        setupTable();
        loadSeekers();
        
        // Set up button event handlers
        addButton.setOnAction(event -> handleAddSeeker());
        updateButton.setOnAction(event -> handleUpdateSeeker());
        deleteButton.setOnAction(event -> handleDeleteSeeker());
        clearButton.setOnAction(event -> clearForm());
        
        // Disable update/delete buttons initially
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        
        // Enable update/delete when a seeker is selected
        seekersTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                updateButton.setDisable(newValue == null);
                deleteButton.setDisable(newValue == null);
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
     * Set up the seekers table with columns and bindings.
     */
    private void setupTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        skillsColumn.setCellValueFactory(new PropertyValueFactory<>("skills"));
        
        seekersTable.setItems(seekerList);
    }

    /**
     * Load seekers from the database into the table.
     */
    private void loadSeekers() {
        try {
            seekerList.clear();
            seekerList.addAll(Database.getAllSeekers());
        } catch (SQLException e) {
            showError("Database Error", "Failed to load seekers: " + e.getMessage());
        }
    }

    /**
     * Handle add seeker button click.
     */
    private void handleAddSeeker() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String skills = skillsArea.getText().trim();
        
        // Validate input
        if (name.isEmpty() || email.isEmpty() || skills.isEmpty()) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }
        
        // Validate email format
        if (!isValidEmail(email)) {
            showError("Validation Error", "Please enter a valid email address.");
            return;
        }
        
        try {
            Seeker seeker = new Seeker(name, email, skills, AppController.getCurrentUser().getId());
            Database.insertSeeker(seeker);
            seekerList.add(seeker);
            clearForm();
            showInfo("Success", "Seeker added successfully.");
        } catch (SQLException e) {
            showError("Database Error", "Failed to add seeker: " + e.getMessage());
        }
    }

    /**
     * Handle update seeker button click.
     */
    private void handleUpdateSeeker() {
        Seeker selectedSeeker = seekersTable.getSelectionModel().getSelectedItem();
        if (selectedSeeker == null) {
            showError("Selection Error", "Please select a seeker to update.");
            return;
        }
        
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String skills = skillsArea.getText().trim();
        
        // Validate input
        if (name.isEmpty() || email.isEmpty() || skills.isEmpty()) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }
        
        // Validate email format
        if (!isValidEmail(email)) {
            showError("Validation Error", "Please enter a valid email address.");
            return;
        }
        
        try {
            selectedSeeker.setName(name);
            selectedSeeker.setEmail(email);
            selectedSeeker.setSkills(skills);
            
            Database.updateSeeker(selectedSeeker);
            int selectedIndex = seekersTable.getSelectionModel().getSelectedIndex();
            seekerList.set(selectedIndex, selectedSeeker);
            showInfo("Success", "Seeker updated successfully.");
        } catch (SQLException e) {
            showError("Database Error", "Failed to update seeker: " + e.getMessage());
        }
    }

    /**
     * Handle delete seeker button click.
     */
    private void handleDeleteSeeker() {
        Seeker selectedSeeker = seekersTable.getSelectionModel().getSelectedItem();
        if (selectedSeeker == null) {
            showError("Selection Error", "Please select a seeker to delete.");
            return;
        }
        
        // Confirm deletion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the seeker '" + selectedSeeker.getName() + "'?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Database.deleteSeeker(selectedSeeker.getId());
                seekerList.remove(selectedSeeker);
                clearForm();
                showInfo("Success", "Seeker deleted successfully.");
            } catch (SQLException e) {
                showError("Database Error", "Failed to delete seeker: " + e.getMessage());
            }
        }
    }

    /**
     * Validate email format using a simple regex.
     * 
     * @param email The email to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Populate the form with data from a seeker.
     * 
     * @param seeker The seeker to populate the form with
     */
    private void populateForm(Seeker seeker) {
        nameField.setText(seeker.getName());
        emailField.setText(seeker.getEmail());
        skillsArea.setText(seeker.getSkills());
    }

    /**
     * Clear the form fields.
     */
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        skillsArea.clear();
        seekersTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
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