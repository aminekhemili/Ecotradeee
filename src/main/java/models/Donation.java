package models;

import java.time.LocalDateTime;

public class Donation {
    private int id;
    private String donorName;
    private double amount;
    private String message;
    private LocalDateTime date;

    public Donation() {}

    public Donation(int id, String donorName, double amount, String message, LocalDateTime date) {
        this.id = id;
        this.donorName = donorName;
        this.amount = amount;
        this.message = message;
        this.date = date;
    }

    public Donation(String donorName, double amount, String message, LocalDateTime date) {
        this.donorName = donorName;
        this.amount = amount;
        this.message = message;
        this.date = date;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}
