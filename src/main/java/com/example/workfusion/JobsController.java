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
 * Controller for the jobs management view.
 * Handles UI interactions and CRUD operations for jobs.
 */
public class JobsController {

    private AppController appController;
    private final ObservableList<Job> jobList = FXCollections.observableArrayList();

    @FXML
    private TableView<Job> jobsTable;

    @FXML
    private TableColumn<Job, String> titleColumn;

    @FXML
    private TableColumn<Job, String> descriptionColumn;

    @FXML
    private TableColumn<Job, String> skillsColumn;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea descriptionArea;

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
        loadJobs();
        
        // Set up button event handlers
        addButton.setOnAction(event -> handleAddJob());
        updateButton.setOnAction(event -> handleUpdateJob());
        deleteButton.setOnAction(event -> handleDeleteJob());
        clearButton.setOnAction(event -> clearForm());
        
        // Disable update/delete buttons initially
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        
        // Enable update/delete when a job is selected
        jobsTable.getSelectionModel().selectedItemProperty().addListener(
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
     * Set up the jobs table with columns and bindings.
     */
    private void setupTable() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        skillsColumn.setCellValueFactory(new PropertyValueFactory<>("skills"));
        
        jobsTable.setItems(jobList);
    }

    /**
     * Load jobs from the database into the table.
     */
    private void loadJobs() {
        try {
            jobList.clear();
            jobList.addAll(Database.getAllJobs());
        } catch (SQLException e) {
            showError("Database Error", "Failed to load jobs: " + e.getMessage());
        }
    }

    /**
     * Handle add job button click.
     */
    private void handleAddJob() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String skills = skillsArea.getText().trim();
        
        // Validate input
        if (title.isEmpty() || description.isEmpty() || skills.isEmpty()) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }
        
        try {
            Job job = new Job(title, description, skills, AppController.getCurrentUser().getId());
            Database.insertJob(job);
            jobList.add(job);
            clearForm();
            showInfo("Success", "Job added successfully.");
        } catch (SQLException e) {
            showError("Database Error", "Failed to add job: " + e.getMessage());
        }
    }

    /**
     * Handle update job button click.
     */
    private void handleUpdateJob() {
        Job selectedJob = jobsTable.getSelectionModel().getSelectedItem();
        if (selectedJob == null) {
            showError("Selection Error", "Please select a job to update.");
            return;
        }
        
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String skills = skillsArea.getText().trim();
        
        // Validate input
        if (title.isEmpty() || description.isEmpty() || skills.isEmpty()) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }
        
        try {
            selectedJob.setTitle(title);
            selectedJob.setDescription(description);
            selectedJob.setSkills(skills);
            
            Database.updateJob(selectedJob);
            int selectedIndex = jobsTable.getSelectionModel().getSelectedIndex();
            jobList.set(selectedIndex, selectedJob);
            showInfo("Success", "Job updated successfully.");
        } catch (SQLException e) {
            showError("Database Error", "Failed to update job: " + e.getMessage());
        }
    }

    /**
     * Handle delete job button click.
     */
    private void handleDeleteJob() {
        Job selectedJob = jobsTable.getSelectionModel().getSelectedItem();
        if (selectedJob == null) {
            showError("Selection Error", "Please select a job to delete.");
            return;
        }
        
        // Confirm deletion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the job '" + selectedJob.getTitle() + "'?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Database.deleteJob(selectedJob.getId());
                jobList.remove(selectedJob);
                clearForm();
                showInfo("Success", "Job deleted successfully.");
            } catch (SQLException e) {
                showError("Database Error", "Failed to delete job: " + e.getMessage());
            }
        }
    }

    /**
     * Populate the form with data from a job.
     * 
     * @param job The job to populate the form with
     */
    private void populateForm(Job job) {
        titleField.setText(job.getTitle());
        descriptionArea.setText(job.getDescription());
        skillsArea.setText(job.getSkills());
    }

    /**
     * Clear the form fields.
     */
    private void clearForm() {
        titleField.clear();
        descriptionArea.clear();
        skillsArea.clear();
        jobsTable.getSelectionModel().clearSelection();
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