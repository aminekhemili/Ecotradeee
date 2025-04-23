package com.ecotradeee.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DonationEventView {
    private int donationId;
    private String donorName;
    private BigDecimal amount;
    private String message;
    private LocalDateTime donationDate;
    private Integer eventId;
    private String eventName;
    private String eventDescription; // Added
    private String eventImage;       // Added
    private LocalDateTime eventDateTime; // Added for potential display/sorting

    // Constructor Updated
    public DonationEventView(int donationId, String donorName, BigDecimal amount, String message, LocalDateTime donationDate,
                             Integer eventId, String eventName, LocalDateTime eventDateTime, String eventDescription, String eventImage) {
        this.donationId = donationId;
        this.donorName = donorName;
        this.amount = amount;
        this.message = message;
        this.donationDate = donationDate;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDateTime = eventDateTime; // Store event date/time
        this.eventDescription = eventDescription;
        this.eventImage = eventImage;
    }

    // Getters (Updated)
    public int getDonationId() { return donationId; }
    public String getDonorName() { return donorName; }
    public BigDecimal getAmount() { return amount; }
    public String getMessage() { return message; }
    public LocalDateTime getDonationDate() { return donationDate; }
    public Integer getEventId() { return eventId; }
    public String getEventName() { return eventName != null ? eventName : "N/A"; }
    public LocalDateTime getEventDateTime() { return eventDateTime; } // Getter for event date/time
    public String getEventDescription() { return eventDescription; }
    public String getEventImage() { return eventImage; }

    // Getter for display name (useful if original eventName is null)
    public String getDisplayEventName() {
        return eventName != null ? eventName : "N/A";
    }
}