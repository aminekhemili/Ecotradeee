package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Donation;
import services.DonationService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UpdateDonationController {

    @FXML
    private TextField donorNameField;

    @FXML
    private TextField amountField;

    @FXML
    private TextArea messageArea;

    @FXML
    private DatePicker datePicker;

    private DonationService donationService = new DonationService();
    private Donation donation;

    public void setDonation(Donation donation) {
        this.donation = donation;


        donorNameField.setText(donation.getDonorName());
        amountField.setText(String.valueOf(donation.getAmount()));
        messageArea.setText(donation.getMessage());
        datePicker.setValue(LocalDate.from(donation.getDate()));
    }

    @FXML
    private void handleUpdateDonation() {
        String donorName = donorNameField.getText();
        String amountText = amountField.getText();
        String message = messageArea.getText();
        LocalDate date = datePicker.getValue();

        if (donorName.isEmpty() || amountText.isEmpty() || date == null) {
            showAlert("All fields are required!");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            donation.setDonorName(donorName);
            donation.setAmount(amount);
            donation.setMessage(message);
            donation.setDate(LocalDateTime.from(date));

            donationService.updateDonation(donation);

            Stage stage = (Stage) donorNameField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert("Amount must be a valid number!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error updating donation: " + e.getMessage());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
