package models;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Event {
    private final IntegerProperty id = new SimpleIntegerProperty(0);
    private final IntegerProperty categoryId = new SimpleIntegerProperty(0);
    private final StringProperty title = new SimpleStringProperty("");
    private final ObjectProperty<LocalDateTime> date = new SimpleObjectProperty<>(LocalDateTime.now());
    private final StringProperty image = new SimpleStringProperty("");
    private final StringProperty description = new SimpleStringProperty("");
    private final ObjectProperty<Category> category = new SimpleObjectProperty<>(new Category());

    // Default constructor with safe initialization
    public Event() {
        this(0, 0, "", LocalDateTime.now(), "", "");
    }

    // Full parameterized constructor
    public Event(int id, int categoryId, String title, LocalDateTime date, String image, String description) {
        this.id.set(id);
        this.categoryId.set(categoryId);
        this.title.set(title != null ? title : "");
        this.date.set(date != null ? date : LocalDateTime.now());
        this.image.set(image != null ? image : "");
        this.description.set(description != null ? description : "");
        this.category.set(new Category(categoryId, ""));
    }

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty categoryIdProperty() { return categoryId; }
    public StringProperty titleProperty() { return title; }
    public ObjectProperty<LocalDateTime> dateProperty() { return date; }
    public StringProperty imageProperty() { return image; }
    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<Category> categoryProperty() { return category; }

    // Standard getters
    public int getId() { return id.get(); }
    public int getCategoryId() { return categoryId.get(); }
    public String getTitle() { return title.get(); }
    public LocalDateTime getDate() { return date.get(); }
    public String getImage() { return image.get(); }
    public String getDescription() { return description.get(); }
    public Category getCategory() { return category.get(); }

    // Setters with null safety
    public void setId(int id) { this.id.set(id); }
    public void setCategoryId(int categoryId) {
        this.categoryId.set(categoryId);
        updateCategoryObject();
    }

    public void setTitle(String title) {
        this.title.set(title != null ? title : "");
    }

    public void setDate(LocalDateTime date) {
        this.date.set(date != null ? date : LocalDateTime.now());
    }

    public void setImage(String image) {
        this.image.set(image != null ? image : "");
    }

    public void setDescription(String description) {
        this.description.set(description != null ? description : "");
    }

    public void setCategory(Category category) {
        if (category != null) {
            this.category.set(category);
            this.categoryId.set(category.getId());
        }
    }

    // Helper method to keep category object and ID in sync
    private void updateCategoryObject() {
        Category current = this.category.get();
        this.category.set(new Category(
                this.categoryId.get(),
                current != null ? current.getName() : ""
        ));
    }
}