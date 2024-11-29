package com.alok.dto;

import java.time.LocalDateTime;

public class Insight {

    private String customerId;
    private String category;
    private int month;
    private int year;
    private String insightMessage;
    private LocalDateTime createdAt;

    public Insight() {}

    public Insight(String customerId, String category, int month, int year, String insightMessage) {
        this.customerId = customerId;
        this.category = category;
        this.month = month;
        this.year = year;
        this.insightMessage = insightMessage;
        this.createdAt = LocalDateTime.now();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getInsightMessage() {
        return insightMessage;
    }

    public void setInsightMessage(String insightMessage) {
        this.insightMessage = insightMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Insight{" +
                "customerId='" + customerId + '\'' +
                ", category='" + category + '\'' +
                ", month=" + month +
                ", year=" + year +
                ", insightMessage='" + insightMessage + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
