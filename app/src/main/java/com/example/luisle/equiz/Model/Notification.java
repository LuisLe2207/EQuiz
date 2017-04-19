package com.example.luisle.equiz.Model;

/**
 * Created by LuisLe on 4/19/2017.
 */

public class Notification {

    private String message;
    private Long startDateTime;
    private Long endDateTime;
    private boolean isActive;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Long startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Long getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Long endDateTime) {
        this.endDateTime = endDateTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Notification() {
       // Firebase required
    }

    public Notification(String message, Long startDateTime, Long endDateTime, boolean isActive) {
        this.message = message;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isActive = isActive;
    }
}
