package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Donation;
import services.DonationService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AddDonationController {

    @FXML
    private TextField donorNameField;

    @FXML
    private TextField amountField;

    @FXML
    private TextArea messageArea;

    @FXML
    private DatePicker datePicker;

    private DonationService donationService = new DonationService();

    @FXML
    private void handleAddDonation() {
        String donorName = donorNameField.getText().trim();
        String amountText = amountField.getText().trim();
        String message = messageArea.getText().trim();
        LocalDate date = datePicker.getValue();

        if (donorName.isEmpty() || amountText.isEmpty() || date == null) {
            showAlert(Alert.AlertType.WARNING, "All fields are required.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            Donation donation = new Donation();
            donation.setDonorName(donorName);
            donation.setAmount(amount);
            donation.setMessage(message);
            donation.setDate(date.atStartOfDay());

            donationService.addDonation(donation);

            showAlert(Alert.AlertType.INFORMATION, "Donation added successfully!");

            // Close the form
            Stage stage = (Stage) donorNameField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Please enter a valid number for amount.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Donation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
