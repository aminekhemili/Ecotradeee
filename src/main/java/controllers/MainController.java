package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import models.Event;
import services.EventService;
import java.time.LocalDateTime;
import java.sql.SQLException;

public class MainController {

    @FXML
    private TextField descriptionField;  // Ensure this is declared properly
    @FXML
    private TextField titleField;
    private final EventService eventService = new EventService();
    private TextField imageField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private void handleAddEvent() {
        try {

            int categoryId = Integer.parseInt(categoryComboBox.getValue());
            String title = titleField.getText();

            LocalDateTime date = LocalDateTime.now();
            String image = imageField.getText();
            String description = descriptionField.getText();
             // Convert selected category to ID

            Event newEvent = new Event(0,categoryId, title,date, image,description   );

            eventService.addEvent(newEvent);
            System.out.println("Event added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void saveEvent() {
        // Your logic to save the event (e.g., call EventService)
        System.out.println("Saving event...");
    }



}
