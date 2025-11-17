package com.example.workfusion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller for the matches view.
 * Handles matching jobs with seekers and displaying results.
 */
public class MatchesController {

    private AppController appController;
    private final ObservableList<Seeker> seekerList = FXCollections.observableArrayList();
    private final ObservableList<Job> jobList = FXCollections.observableArrayList();
    private final ObservableList<Matcher.MatchResult> matchResults = FXCollections.observableArrayList();

    @FXML
    private ComboBox<Seeker> seekerComboBox;

    @FXML
    private ComboBox<Job> jobComboBox;

    @FXML
    private Button matchSeekerButton;

    @FXML
    private Button matchJobButton;

    @FXML
    private Button matchAllButton;

    @FXML
    private TableView<Matcher.MatchResult> resultsTable;

    @FXML
    private TableColumn<Matcher.MatchResult, String> entityColumn;

    @FXML
    private TableColumn<Matcher.MatchResult, Integer> overlapCountColumn;

    @FXML
    private TableColumn<Matcher.MatchResult, Integer> totalJobSkillsColumn;

    @FXML
    private TableColumn<Matcher.MatchResult, Integer> totalSeekerSkillsColumn;

    @FXML
    private TableColumn<Matcher.MatchResult, Integer> scoreColumn;

    /**
     * Initialize method called by JavaFX after FXML elements are injected.
     * Sets up the UI components and loads initial data.
     */
    @FXML
    public void initialize() {
        setupTable();
        loadSeekersAndJobs();
        
        // Set up button event handlers
        matchSeekerButton.setOnAction(event -> matchSelectedSeeker());
        matchJobButton.setOnAction(event -> matchSelectedJob());
        matchAllButton.setOnAction(event -> matchAll());
        
        // Set up combo boxes
        seekerComboBox.setItems(seekerList);
        jobComboBox.setItems(jobList);
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
     * Set up the results table with columns and bindings.
     */
    private void setupTable() {
        entityColumn.setCellValueFactory(cellData -> {
            Matcher.MatchResult result = cellData.getValue();
            if (result.getSeeker() != null && result.getJob() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    "Seeker: " + result.getSeeker().getName() + " vs Job: " + result.getJob().getTitle());
            } else {
                return new javafx.beans.property.SimpleStringProperty("Match Result");
            }
        });
        
        overlapCountColumn.setCellValueFactory(new PropertyValueFactory<>("overlapCount"));
        totalJobSkillsColumn.setCellValueFactory(new PropertyValueFactory<>("totalJobSkills"));
        totalSeekerSkillsColumn.setCellValueFactory(new PropertyValueFactory<>("totalSeekerSkills"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        
        resultsTable.setItems(matchResults);
    }

    /**
     * Load seekers and jobs from the database.
     */
    private void loadSeekersAndJobs() {
        try {
            seekerList.clear();
            jobList.clear();
            
            seekerList.addAll(Database.getAllSeekers());
            jobList.addAll(Database.getAllJobs());
        } catch (SQLException e) {
            showError("Database Error", "Failed to load data: " + e.getMessage());
        }
    }

    /**
     * Match the selected seeker against all jobs.
     */
    private void matchSelectedSeeker() {
        Seeker selectedSeeker = seekerComboBox.getValue();
        if (selectedSeeker == null) {
            showError("Selection Error", "Please select a seeker to match.");
            return;
        }
        
        try {
            List<Job> jobs = Database.getAllJobs();
            List<Matcher.MatchResult> results = Matcher.matchSeekerWithJobs(selectedSeeker, jobs);
            matchResults.clear();
            matchResults.addAll(results);
        } catch (SQLException e) {
            showError("Database Error", "Failed to perform matching: " + e.getMessage());
        }
    }

    /**
     * Match the selected job against all seekers.
     */
    private void matchSelectedJob() {
        Job selectedJob = jobComboBox.getValue();
        if (selectedJob == null) {
            showError("Selection Error", "Please select a job to match.");
            return;
        }
        
        try {
            List<Seeker> seekers = Database.getAllSeekers();
            List<Matcher.MatchResult> results = Matcher.matchJobWithSeekers(selectedJob, seekers);
            matchResults.clear();
            matchResults.addAll(results);
        } catch (SQLException e) {
            showError("Database Error", "Failed to perform matching: " + e.getMessage());
        }
    }

    /**
     * Match all seekers with all jobs.
     */
    private void matchAll() {
        try {
            List<Seeker> seekers = Database.getAllSeekers();
            List<Job> jobs = Database.getAllJobs();
            
            List<Matcher.MatchResult> results = Matcher.matchAll(seekers, jobs);
            matchResults.clear();
            matchResults.addAll(results);
        } catch (SQLException e) {
            showError("Database Error", "Failed to perform matching: " + e.getMessage());
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