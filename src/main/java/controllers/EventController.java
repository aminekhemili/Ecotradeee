package controllers;

import database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Event;
import services.EventService;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class EventController implements Initializable {

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, Integer> idColumn;
    @FXML private TableColumn<Event, Integer> category_idColumn;
    @FXML private TableColumn<Event, String> titleColumn;
    @FXML private TableColumn<Event, LocalDateTime> dateColumn;
    @FXML private TableColumn<Event, String> imageColumn;
    @FXML private TableColumn<Event, String> descriptionColumn;

    private final ObservableList<Event> eventList = FXCollections.observableArrayList();
    private final EventService eventService = new EventService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadEvents();
    }

    private void loadEvents() {
        eventList.clear();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM event ")) {

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("title"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("image"),
                        rs.getString("description")
                );
                eventList.add(event);
            }


            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            category_idColumn.setCellValueFactory(new PropertyValueFactory<>("category_id"));
            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

            eventTable.setItems(eventList);

         } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading events from the database.");
        }
    }

    @FXML
    private void handleAddEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add_event.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Event");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadEvents();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error opening the Add Event form.");
        }
    }

    @FXML
    private void handleUpdateEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an event to update.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add_event.fxml"));
            Parent root = loader.load();
            EventListController controller = loader.getController();
            //controller.setEvent(selected);

            Stage stage = new Stage();
            stage.setTitle("Update Event");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadEvents();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error opening the Update Event form.");
        }
    }

    @FXML
    private void handleDeleteEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an event to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Event");
        confirm.setHeaderText("Are you sure?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    eventService.deleteEvent(selected.getId());
                    loadEvents();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Failed to delete the event.");
                }
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
