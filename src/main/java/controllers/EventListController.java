package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Event;
import services.EventService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class EventListController {

    @FXML
    private TableView<Event> eventTableView;
    @FXML
    private TableColumn<Event, Integer> idColumn;
    @FXML
    private TableColumn<Event, String> titleColumn;
    @FXML
    private TableColumn<Event, String> categoryColumn;
    @FXML
    private TableColumn<Event, String> dateColumn;
    @FXML
    private TableColumn<Event, String> descriptionColumn;
    @FXML
    private TableColumn<Event, String> imageColumn;

    private final EventService eventService = new EventService();
    private final ObservableList<Event> events = FXCollections.observableArrayList();
    private Stage primaryStage;


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty()); // Re-enabled title column
        categoryColumn.setCellValueFactory(cellData -> {

            if (cellData.getValue().getCategory() != null) {
                return new SimpleStringProperty(cellData.getValue().getCategory().getName());

            } else {
                return new SimpleStringProperty("");
            }
                });
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDate() != null ? cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : ""
        ));
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        imageColumn.setCellValueFactory(cellData -> cellData.getValue().imageProperty());

        try {
            loadEvents();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Startup Error", "Failed to load events during startup: " + e.getMessage());
            e.printStackTrace();
        }

        eventTableView.setItems(events);
    }


    private void loadEvents() throws SQLException {
        events.clear();
        events.addAll(eventService.getAllEvents());
    }




    @FXML
    void handleEditEvent(ActionEvent event) throws IOException {
        Event selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            openEventForm(selectedEvent);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select an event to edit.");
        }
    }
    @FXML
    private void goToCategoryList(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ecotradeee/views/category_list.fxml")); // Replace with your actual FXML file name
        Parent categoryListRoot = loader.load();

        Stage categoryListStage = new Stage();
        categoryListStage.setTitle("Category Management");
        categoryListStage.setScene(new Scene(categoryListRoot));
        categoryListStage.initModality(Modality.WINDOW_MODAL);
        categoryListStage.initOwner(primaryStage); // Use the main window as the owner
        categoryListStage.show();
    }
    @FXML
    private void handleAddEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ecotradeee/views/EventForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Event");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadEvents();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert("Error opening the Add Event form.");
        }

    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void handleDeleteEvent(ActionEvent event) throws SQLException {
        Event selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Deletion");
            confirmationDialog.setHeaderText("Are you sure you want to delete this event?");
            confirmationDialog.setContentText("Event: " + selectedEvent.getTitle());

            confirmationDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        eventService.deleteEvent(selectedEvent.getId());
                        loadEvents(); // Refresh the table view after deletion.
                        showAlert(Alert.AlertType.INFORMATION, "Event Deleted", "Event '" + selectedEvent.getTitle() + "' has been deleted.");
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete event: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Required", "Please select an event to delete.");
        }
    }


    private void openEventForm(Event eventToEdit) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ecotradeee/views/EventForm.fxml"));
        Parent root = loader.load();

        EventFormController controller = loader.getController();
        Stage dialogStage = new Stage();
        controller.setDialogStage(dialogStage);
        controller.setEvent(eventToEdit);

        dialogStage.setTitle(eventToEdit == null ? "Add New Event" : "Edit Event");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        dialogStage.setScene(new Scene(root));

        dialogStage.showAndWait();
        try {
            loadEvents();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}