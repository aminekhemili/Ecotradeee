// In your main application class file (e.g., App.java or MainApp.java)

package controllers; // Make sure this matches your package

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

public class Main extends EventController { // Or whatever your class is named

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Load the main event list FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/event_list.fxml"));
            Parent root = loader.load();

            // Get the controller instance
            EventController eventController = loader.getController();

            // Pass the primary stage to the controller (useful for modal windows)
            eventController.setPrimaryStage(primaryStage);

            // Set up the scene and display the primary stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Event Management"); // Set your window title
            primaryStage.show();

        } catch (IOException e) {
            // Handle errors during FXML loading
            System.err.println("Error loading event_list.fxml");
            e.printStackTrace();
            // Show an alert to the user
            // You might need a simple way to show alerts without a full controller loaded yet
            // Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load application UI.");
            // alert.showAndWait();
        } catch (Exception e) {
            // Catch any other unexpected errors during startup
            System.err.println("An unexpected error occurred during application startup.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // This launches the JavaFX application
    }
}