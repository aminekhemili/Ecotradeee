package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Category;
import models.Event;
import services.CategoryService;
import services.EventService;
import controllers.EventController;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class EventFormController {

    @FXML
    private TextField titleField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private DatePicker datePicker;


    @FXML
    private TextField imagePathField;

    @FXML
    private Button browseImageButton;

    @FXML
    private ImageView eventImageView;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private Stage dialogStage;
    private Event event;
    private boolean isNewEvent = true;
    private final EventService eventService = new EventService();
    private final CategoryService categoryService = new CategoryService();
    private final ObservableList<Category> categories = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            loadCategories();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error loading categories", e.getMessage());
            e.printStackTrace();
        }
        categoryComboBox.setItems(categories);

        browseImageButton.setOnAction(e -> browseImage());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEvent(Event event) {
        this.event = event;
        if (event != null) {
            isNewEvent = false;
            titleField.setText(event.getTitle());
            categoryComboBox.setValue(event.getCategory());
            datePicker.setValue(event.getDate().toLocalDate());
            //timeField.setText(event.getDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            imagePathField.setText(event.getImage());
            loadImage(event.getImage());
            descriptionArea.setText(event.getDescription());
        } else {
            this.event = new Event(); // Create a new empty event
            isNewEvent = true;
        }
    }

    private void loadCategories() throws SQLException {
        categories.clear();
        List<Category> categoryList = categoryService.getAllCategories();
        categories.addAll(categoryList);
    }

    @FXML
    private void browseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Event Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(dialogStage);
        if (selectedFile != null) {
            imagePathField.setText(selectedFile.getAbsolutePath());
            loadImage(selectedFile.getAbsolutePath());
        }
    }

    private void loadImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                try {
                    Image image = new Image(file.toURI().toString());
                    eventImageView.setImage(image);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error loading image", "Could not load image from: " + imagePath);
                    e.printStackTrace();
                }
            } else {
                eventImageView.setImage(null); // Clear image if file not found
            }
        } else {
            eventImageView.setImage(null);
        }
    }

    @FXML
    private void handleSave() {
        if (event == null) {
            event = new Event();
            isNewEvent = true;

        }
        if (isInputValid()) {
            event.setTitle(titleField.getText());
            event.setCategoryId(categoryComboBox.getValue().getId());
            try {
                LocalDate selectedDate = datePicker.getValue();
                if (selectedDate != null) {
                    event.setDate(LocalDateTime.of(selectedDate, LocalTime.MIDNIGHT));
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Date", "Please select a valid date.");
                    return;
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Date", "Please select a valid date.");
                return;
            }
            event.setImage(imagePathField.getText());
            event.setDescription(descriptionArea.getText());

            try {
                if (isNewEvent) {
                    eventService.addEvent(event);
                    showAlert(Alert.AlertType.INFORMATION, "Event Added", "Event has been added successfully.");
                } else {
                    eventService.updateEvent(event); // This is the UPDATE part
                    showAlert(Alert.AlertType.INFORMATION, "Event Updated", "Event has been updated successfully.");
                }
                dialogStage.close();
                saveButton.getScene().getWindow().fireEvent(new ActionEvent()); // Trigger refresh
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }
    private boolean isInputValid() {
        String errorMessage = "";
        if (titleField.getText() == null || titleField.getText().isEmpty()) {
            errorMessage += "Title is required!\n";
        }
        if (categoryComboBox.getValue() == null) {
            errorMessage += "Category must be selected!\n";
        }
        if (datePicker.getValue() == null) {
            errorMessage += "Date is required!\n";
        }

        if (descriptionArea.getText() == null || descriptionArea.getText().isEmpty()) {
            errorMessage += "Description is required!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid Fields", errorMessage);
            return false;
        }
    }
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}