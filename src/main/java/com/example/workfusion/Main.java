package com.example.workfusion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Main application class that launches the JavaFX application.
 * This is the entry point of the application.
 */
public class Main extends Application {

    /**
     * Start method called by JavaFX when the application launches.
     * Initializes the database and loads the login scene.
     * 
     * @param primaryStage The primary stage for the application
     * @throws Exception if there's an error loading the FXML or initializing components
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Run database migrations to ensure tables exist
        Database.runMigrations();
        
        // Load the login scene first
        System.out.println("Attempting to load login.fxml");
        URL fxmlUrl = getClass().getResource("/com/example/workfusion/login.fxml");
        System.out.println("FXML URL: " + fxmlUrl);
        
        if (fxmlUrl == null) {
            throw new RuntimeException("Could not find login.fxml in the classpath");
        }
        
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fxmlUrl);
        Parent root = loader.load();
        Scene scene = new Scene(root, 400, 300);
        
        // Apply CSS styling
        URL cssUrl = getClass().getResource("/com/example/workfusion/app-theme.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        
        // Apply Fancy Pro Max theme
        URL themeUrl = getClass().getResource("/css/theme.css");
        if (themeUrl != null) {
            scene.getStylesheets().add(themeUrl.toExternalForm());
        }
        
        primaryStage.setTitle("Delphi Minds - Login");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);
        primaryStage.show();
    }

    /**
     * Main method - entry point of the application.
     * Launches the JavaFX application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}