package models;

public class Category {
    private int id;
    private String name;

    // Default constructor
    public Category() {
    }

    // Constructor for new categories (no ID yet)
    public Category(String name) {
        this.name = name;
    }

    // Constructor for full category (with ID)
    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // For TableView binding (if needed later)
    public javafx.beans.property.IntegerProperty idProperty() {
        return new javafx.beans.property.SimpleIntegerProperty(id);
    }

    public javafx.beans.property.StringProperty nameProperty() {
        return new javafx.beans.property.SimpleStringProperty(name);
    }
    @Override
    public String toString() {
        return name;
    }
}
