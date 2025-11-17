package com.example.workfusion;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ItemsController {

    private AppController appController;
    private final ObservableList<Item> itemList = FXCollections.observableArrayList();
    private final ItemDao itemDao = new ItemDao();

    @FXML
    private TableView<Item> itemsTable;

    @FXML
    private TableColumn<Item, Integer> idColumn;

    @FXML
    private TableColumn<Item, String> nameColumn;

    @FXML
    private TableColumn<Item, String> descriptionColumn;

    @FXML
    private TableColumn<Item, Integer> quantityColumn;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField quantityField;

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
        refreshTable();

        // Set up button event handlers
        addButton.setOnAction(event -> handleAddItem());
        updateButton.setOnAction(event -> handleUpdateItem());
        deleteButton.setOnAction(event -> handleDeleteItem());
        clearButton.setOnAction(event -> clearForm());

        // Disable update/delete buttons initially
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        // Enable update/delete when an item is selected
        itemsTable.getSelectionModel().selectedItemProperty().addListener(
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
     * Set up the items table with columns and bindings.
     */
    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        itemsTable.setItems(itemList);
    }

    /**
     * Refresh the table with data from the database.
     * Runs the database operation on a background thread.
     */
    public void refreshTable() {
        Task<List<Item>> task = new Task<>() {
            @Override
            protected List<Item> call() throws Exception {
                System.out.println("DEBUG: Loading items from database");
                return itemDao.readAll();
            }
        };

        task.setOnSucceeded(event -> {
            itemList.setAll(task.getValue());
            System.out.println("DEBUG: Loaded " + itemList.size() + " items from database");
        });

        task.setOnFailed(event -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            showError("Failed to load items", "Failed to load items from database: " + ex.getMessage());
        });

        new Thread(task, "db-reader").start();
    }

    /**
     * Handle add item button click.
     */
    private void handleAddItem() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String quantityStr = quantityField.getText().trim();

        // Validate input
        if (name.isEmpty() || description.isEmpty() || quantityStr.isEmpty()) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            showError("Validation Error", "Quantity must be a valid number.");
            return;
        }

        try {
            Item item = new Item(name, description, quantity);
            Item createdItem = itemDao.create(item);
            itemList.add(createdItem);
            clearForm();
            showInfo("Success", "Item added successfully.");
        } catch (SQLException e) {
            showError("Database Error", "Failed to add item: " + e.getMessage());
        }
    }

    /**
     * Handle update item button click.
     */
    private void handleUpdateItem() {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("Selection Error", "Please select an item to update.");
            return;
        }

        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String quantityStr = quantityField.getText().trim();

        // Validate input
        if (name.isEmpty() || description.isEmpty() || quantityStr.isEmpty()) {
            showError("Validation Error", "Please fill in all fields.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            showError("Validation Error", "Quantity must be a valid number.");
            return;
        }

        try {
            selectedItem.setName(name);
            selectedItem.setDescription(description);
            selectedItem.setQuantity(quantity);

            boolean updated = itemDao.update(selectedItem);
            if (updated) {
                int selectedIndex = itemsTable.getSelectionModel().getSelectedIndex();
                itemList.set(selectedIndex, selectedItem);
                showInfo("Success", "Item updated successfully.");
            } else {
                showError("Update Error", "Failed to update item.");
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to update item: " + e.getMessage());
        }
    }

    /**
     * Handle delete item button click.
     */
    private void handleDeleteItem() {
        Item selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showError("Selection Error", "Please select an item to delete.");
            return;
        }

        // Confirm deletion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the item '" + selectedItem.getName() + "'?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = itemDao.delete(selectedItem.getId());
                if (deleted) {
                    itemList.remove(selectedItem);
                    clearForm();
                    showInfo("Success", "Item deleted successfully.");
                } else {
                    showError("Delete Error", "Failed to delete item.");
                }
            } catch (SQLException e) {
                showError("Database Error", "Failed to delete item: " + e.getMessage());
            }
        }
    }

    /**
     * Populate the form with data from an item.
     *
     * @param item The item to populate the form with
     */
    private void populateForm(Item item) {
        nameField.setText(item.getName());
        descriptionArea.setText(item.getDescription());
        quantityField.setText(String.valueOf(item.getQuantity()));
    }

    /**
     * Clear the form fields.
     */
    private void clearForm() {
        nameField.clear();
        descriptionArea.clear();
        quantityField.clear();
        itemsTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    /**
     * Show an error dialog to the user.
     *
     * @param title   The title of the error dialog
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
     * @param title   The title of the information dialog
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