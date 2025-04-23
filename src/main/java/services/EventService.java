package services;

import database.DatabaseConnection;
import models.Event;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    public void addEvent(Event event) throws SQLException {
        String query = "INSERT INTO event (category_id, title, date, image, description ) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, event.getCategoryId());
            pstmt.setString(2, event.getTitle());
            pstmt.setString(3, event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            pstmt.setString(4, event.getImage());
            pstmt.setString(5, event.getDescription());



            pstmt.executeUpdate();
        }
    }

    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT event.*, category.name AS category_name FROM event INNER JOIN category ON event.category_id = category.id";

        try (Connection conn = DatabaseConnection.getConnection(); // make sure this method is correct
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int categoryId = rs.getInt("category_id");
                String description = rs.getString("description");
                String dateString = rs.getString("date");
                LocalDateTime date = null;

                // Try-catch for safe date parsing
                try {
                    date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String categoryName = rs.getString("category_name");
                String image = rs.getString("image");

                // Optional: use categoryName in your Event model
                Event event = new Event(id, categoryId, title,date,  image,description );
                // event.setCategoryName(categoryName); // if you add that field

                events.add(event);
            }
        }

        return events;
    }
    public void deleteEvent(int eventId) throws SQLException {
        String query = "DELETE FROM event WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        }
    }
    public void updateEvent(Event event) {
        String query = "UPDATE event SET category_id = ?, title = ?, date = ?, image = ?, description = ?, like_count = ?, dislike_count = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, event.getCategoryId());
            stmt.setString(2, event.getTitle());
            stmt.setTimestamp(3, Timestamp.valueOf(event.getDate()));
            stmt.setString(4, event.getImage());
            stmt.setString(5, event.getDescription());
            stmt.setInt(8, event.getId());
            int rowsAffected = stmt.executeUpdate();
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
