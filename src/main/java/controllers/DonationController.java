package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Donation;
import services.DonationService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class DonationController implements Initializable {

    @FXML private TableView<Donation> donationTable;
    @FXML private TableColumn<Donation, Integer> idColumn;
    @FXML private TableColumn<Donation, String> donorColumn;
    @FXML private TableColumn<Donation, Double> amountColumn;
    @FXML private TableColumn<Donation, String> messageColumn;
    @FXML private TableColumn<Donation, LocalDate> dateColumn;
    @FXML private TextField searchField;

    private final DonationService donationService = new DonationService();
    private ObservableList<Donation> donations = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupColumns();
        loadDonations();
    }

    private void setupColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        donorColumn.setCellValueFactory(new PropertyValueFactory<>("donorName"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void loadDonations() {
        try {
            List<Donation> list = donationService.getAllDonations();
            donations.setAll(list);
            donationTable.setItems(donations);
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleAddDonation() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/add_donation.fxml"));
        AnchorPane pane = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(pane));
        stage.setTitle("Add Donation");
        stage.showAndWait();
        loadDonations();
    }

    @FXML
    private void handleUpdateDonation() throws IOException {
        Donation selected = donationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No selection", "Please select a donation to update.");
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/update_donation.fxml"));
        AnchorPane pane = loader.load();
        UpdateDonationController controller = loader.getController();
        controller.setDonation(selected);
        Stage stage = new Stage();
        stage.setScene(new Scene(pane));
        stage.setTitle("Update Donation");
        stage.showAndWait();
        loadDonations();
    }

    @FXML
    private void handleDeleteDonation() {
        Donation selected = donationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No selection", "Please select a donation to delete.");
            return;
        }
        try {
            donationService.deleteDonation(selected.getId());
            loadDonations();
        } catch (SQLException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            loadDonations();
            return;
        }
        ObservableList<Donation> filtered = FXCollections.observableArrayList();
        for (Donation d : donations) {
            if (d.getDonorName().toLowerCase().contains(keyword) ||
                    d.getMessage().toLowerCase().contains(keyword)) {
                filtered.add(d);
            }
        }
        donationTable.setItems(filtered);
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
