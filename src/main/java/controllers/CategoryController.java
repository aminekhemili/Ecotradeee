 package controllers; // Keep the package

import models.Category;
// Import the service
import services.CategoryService;

// DatabaseUtil is no longer directly needed here, but keep if showAlert uses it indirectly, or remove
// import com.yourcompany.categorycrud.util.DatabaseUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

import static database.DatabaseConnection.getConnection;
import static services.CategoryService.getAllCategories;

 public class CategoryController implements Initializable {

    // --- FXML Fields (remain the same) ---
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> idColumn;
    @FXML private TableColumn<Category, String> nameColumn;
    @FXML private TextField nameField;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    private ObservableList<Category> categoryList;
    private Category selectedCategory = null;

    // --- Add an instance of the Service ---
    private final CategoryService categoryService = new CategoryService(); // Instantiate the service

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure columns (remains the same)
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Load Data using the service
        loadCategories();

        // Listener (remains the same)
        categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedCategory = newSelection;
                nameField.setText(selectedCategory.getName());
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                clearSelection();
            }
        });

        // Initial Button State (remains the same)
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    // --- Update Button Actions to use CategoryService ---

    @FXML
    private void handleAddButton() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) { // Basic UI validation can stay here
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Category name cannot be empty.");
            return;
        }

        // Use the service to add
        Category newCategory = categoryService.addCategory(name); // <--- Changed

        if (newCategory != null) {
            categoryList.add(newCategory);
            categoryTable.getSelectionModel().select(newCategory);
            categoryTable.scrollTo(newCategory);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Category '" + name + "' added successfully.");
            clearFields();
        } else {
            // Service layer might provide more specific reasons, or controller checks DBUtil logs
            showAlert(Alert.AlertType.ERROR, "Operation Failed", "Failed to add category. It might already exist or an error occurred.");
        }
    }

    @FXML
    private void handleUpdateButton() {
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a category to update.");
            return;
        }

        String updatedName = nameField.getText().trim();
        if (updatedName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Category name cannot be empty.");
            return;
        }
        if (updatedName.equals(selectedCategory.getName())) {
            showAlert(Alert.AlertType.INFORMATION, "No Change", "The category name has not been changed.");
            return;
        }

        // Create a temporary object or update the existing one BEFORE calling service
        // It's often better to pass necessary data rather than the bound object directly
        // For simplicity here, we update the bound object first:
        selectedCategory.setName(updatedName);

        // Use the service to update
        boolean success = categoryService.updateCategory(selectedCategory); // <--- Changed

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Category updated successfully.");
            categoryTable.refresh(); // Refresh the view
            categoryTable.getSelectionModel().select(selectedCategory); // Re-select
        } else {
            // If update failed (e.g., duplicate name), we might need to revert the change
            // in the selectedCategory object or reload data fully.
            showAlert(Alert.AlertType.ERROR, "Operation Failed", "Failed to update category. The name might already exist or an error occurred.");
            // Reload data to ensure consistency after failed update
            loadCategories(); // Safer option after a failed update
            // selectedCategory might be invalid now if list reloaded, so clear selection
            clearSelection();
        }
    }
     public static Category addCategory(String name) {
         String sql = "INSERT INTO category (name) VALUES (?)";
         ResultSet generatedKeys = null;
         Category newCategory = null;

         try (Connection conn = getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

             pstmt.setString(1, name);
             int affectedRows = pstmt.executeUpdate();

             if (affectedRows > 0) {
                 generatedKeys = pstmt.getGeneratedKeys();
                 if (generatedKeys.next()) {
                     int newId = generatedKeys.getInt(1);
                     newCategory = new Category(newId, name);
                     System.out.println("Category added successfully with ID: " + newId);
                 }
             }
         } catch (SQLException e) {
             System.err.println("Error adding category: " + e.getMessage());
             e.printStackTrace(); // Handle error (e.g., show error dialog)
             // Check for duplicate entry (based on UNIQUE constraint on name)
             if (e.getMessage().toLowerCase().contains("duplicate entry")) {
                 // Optionally, return a specific error code or throw a custom exception
                 System.err.println("Category '" + name + "' already exists.");
             }
         } finally {
             if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
         }
         return newCategory; // Return the created category (or null if failed)
     }

     public static boolean updateCategory(Category category) {
         String sql = "UPDATE category SET name = ? WHERE id = ?";
         boolean success = false;

         if (category == null || category.getId() <= 0) {
             System.err.println("Error: Invalid category data for update.");
             return false;
         }

         try (Connection conn = getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {

             pstmt.setString(1, category.getName());
             pstmt.setInt(2, category.getId());

             int affectedRows = pstmt.executeUpdate();
             success = affectedRows > 0;
             if(success) {
                 System.out.println("Category updated successfully: ID " + category.getId());
             } else {
                 System.out.println("Category update failed or no changes made for ID: " + category.getId());
             }

         } catch (SQLException e) {
             System.err.println("Error updating category: " + e.getMessage());
             e.printStackTrace(); // Handle error
             if (e.getMessage().toLowerCase().contains("duplicate entry")) {
                 System.err.println("Cannot update: Category name '" + category.getName() + "' already exists for another ID.");
             }
         }
         return success;
     }
    public static boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM category WHERE id = ?";
        boolean success = false;

        if (categoryId <= 0) {
            System.err.println("Error: Invalid category ID for deletion.");
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            int affectedRows = pstmt.executeUpdate();
            success = affectedRows > 0;
            if(success) {
                System.out.println("Category deleted successfully: ID " + categoryId);
            } else {
                System.out.println("Category deletion failed (likely ID not found): " + categoryId);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());

            if (e.getErrorCode() == 1451 ) {
                System.err.println("Cannot delete category ID " + categoryId + ": It is referenced by other records.");
            }
            e.printStackTrace();
        }
        return success;
    }
    @FXML
    private void handleDeleteButton() {
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a category to delete.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);

        confirmationAlert.setTitle("Confirm Deletion");
        confirmationAlert.setHeaderText("Delete Category: " + selectedCategory.getName());
        confirmationAlert.setContentText("Are you sure you want to delete this category?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = categoryService.deleteCategory(selectedCategory.getId());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Category deleted successfully.");

                categoryList.remove(selectedCategory);

                clearSelection();
            } else {
                showAlert(Alert.AlertType.ERROR, "Operation Failed", "Failed to delete category. It might be in use or an error occurred.");

            }
        }
    }

    @FXML
    private void handleClearButton() {
        clearSelection();
    }



    private void loadCategories() {

        categoryList = categoryService.getAllCategories();
        categoryTable.setItems(categoryList);
        System.out.println("Categories loaded/reloaded via Service.");
    }

    private void clearFields() {
        nameField.clear();
    }

    private void clearSelection() {
        selectedCategory = null;
        categoryTable.getSelectionModel().clearSelection();
        nameField.clear();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}