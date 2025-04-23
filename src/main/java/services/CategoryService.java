package services; // Optional: put in a 'service' subpackage

import controllers.CategoryController;
import javafx.collections.FXCollections;
import models.Category;
import database.DatabaseConnection;
import database.DatabaseConnection;
import javafx.collections.ObservableList;

import java.sql.*;

import static database.DatabaseConnection.getConnection;

/**
 * Service layer for managing Category data.
 * Acts as an intermediary between the Controller and the Database Utility/DAO.
 * Can contain business logic related to categories.
 */
public class CategoryService {


    public Category getCategoryById(int id) throws SQLException {
        String query = "SELECT * FROM category  WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("id"), rs.getString("name"));
            }
        }
        return null;
    }

    /**
     * Adds a new category after potentially applying business rules/validation.
     * @param name The name of the category to add.
     * @return The newly created Category object with its ID, or null if failed.
     */
    public Category addCategory(String name) {
        // Example: Add business logic - maybe enforce naming conventions?
        if (name == null || name.trim().isEmpty()) {
            System.err.println("SERVICE LAYER: Category name cannot be empty.");
            // Could throw a custom validation exception here
            return null;
        }
        String processedName = name.trim(); // Example processing

        // Delegate persistence to the data access layer
        Category newCategory = CategoryController.addCategory(processedName);

        // Could perform post-add actions here (e.g., logging, notifications)
        if (newCategory != null) {
            System.out.println("SERVICE LAYER: Category '" + processedName + "' added successfully.");
        } else {
            System.out.println("SERVICE LAYER: Failed to add category '" + processedName + "'.");
        }
        return newCategory;
    }

    public static ObservableList<Category> getAllCategories() {
        ObservableList<Category> categoryList = FXCollections.observableArrayList();
        String query = "SELECT id, name FROM category ORDER BY name"; // Order for better display

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                categoryList.add(new Category(id, name));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
            e.printStackTrace(); // Handle error appropriately (e.g., show error dialog)
        }
        return categoryList;
    }

    public boolean updateCategory(Category category) {
        // Add validation or business rules before updating
        if (category == null || category.getId() <= 0) {
            System.err.println("SERVICE LAYER: Invalid category data for update.");
            return false;
        }
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            System.err.println("SERVICE LAYER: Category name cannot be empty for update.");
            return false;
        }
        category.setName(category.getName().trim()); // Ensure trimmed name

        // Delegate persistence
        boolean success = CategoryController.updateCategory(category);

        if (success) {
            System.out.println("SERVICE LAYER: Category ID " + category.getId() + " updated successfully.");
        } else {
            System.out.println("SERVICE LAYER: Failed to update category ID " + category.getId() + ".");
        }
        return success;
    }

    /**
     * Deletes a category by its ID.
     * @param categoryId The ID of the category to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteCategory(int categoryId) {
        if (categoryId <= 0) {
            System.err.println("SERVICE LAYER: Invalid category ID for deletion.");
            return false;
        }

        // Could add checks here, e.g., prevent deletion of 'default' categories

        // Delegate persistence
        boolean success = CategoryController.deleteCategory(categoryId);

        if (success) {
            System.out.println("SERVICE LAYER: Category ID " + categoryId + " deleted successfully.");
        } else {
            System.out.println("SERVICE LAYER: Failed to delete category ID " + categoryId + ".");
        }
        return success;
    }
}