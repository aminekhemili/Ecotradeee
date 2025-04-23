package services;

import database.DatabaseConnection;
import models.Donation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DonationService {

    public void addDonation(Donation donation) throws SQLException {
        String sql = "INSERT INTO donation (id, donor_name, amount) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, donation.getId());
            stmt.setString(2, donation.getDonorName());
            stmt.setDouble(3, donation.getAmount());
            stmt.executeUpdate();
        }
    }

    public List<Donation> getAllDonations() throws SQLException {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT * FROM donation";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Donation(
                        rs.getInt("id"),
                        rs.getString("donor_name"),
                        rs.getDouble("amount"),
                        rs.getString("message"),
                        rs.getTimestamp("donation_date").toLocalDateTime()
                ));
            }
        }
        return list;
    }

    public void updateDonation(Donation donation) throws SQLException {
        String sql = "UPDATE donation SET event_id=?, donor_name=?, amount=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, donation.getId());
            stmt.setString(2, donation.getDonorName());
            stmt.setDouble(3, donation.getAmount());
            stmt.setInt(4, donation.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteDonation(int id) throws SQLException {
        String sql = "DELETE FROM donation WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Donation> searchDonations(String keyword) throws SQLException {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT * FROM donation WHERE donor_name LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Donation(
                        rs.getInt("id"),
                        rs.getString("donor_name"),
                        rs.getDouble("amount"),
                        rs.getString("message"),
                        rs.getTimestamp("donation_date").toLocalDateTime()
                ));
            }
        }
        return list;
    }

    public List<Donation> sortDonationsByAmount() throws SQLException {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT * FROM donation ORDER BY amount DESC";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Donation(
                        rs.getInt("id"),
                        rs.getString("donor_name"),
                        rs.getDouble("amount"),
                        rs.getString("message"),
                        rs.getTimestamp("donation_date").toLocalDateTime()
                ));
            }
        }
        return list;
    }
}
