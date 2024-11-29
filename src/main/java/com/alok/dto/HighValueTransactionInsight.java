package com.alok.dto;

import java.time.LocalDateTime;

public class HighValueTransactionInsight {

    private String customerId;
    private String category;
    private String transactionType;
    private double transactionAmount;
    private String timePeriod;
    private LocalDateTime periodDate;
    private String insightMessage;
    private LocalDateTime createdAt;

    public HighValueTransactionInsight() {}

    public HighValueTransactionInsight(String customerId, String category, String transactionType, double transactionAmount, String timePeriod, LocalDateTime periodDate, String insightMessage) {
        this.customerId = customerId;
        this.category = category;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.timePeriod = timePeriod;
        this.periodDate = periodDate;
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

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public LocalDateTime getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(LocalDateTime periodDate) {
        this.periodDate = periodDate;
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
        return "HighValueTransactionInsight{" +
                "customerId='" + customerId + '\'' +
                ", category='" + category + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", timePeriod='" + timePeriod + '\'' +
                ", periodDate=" + periodDate +
                ", insightMessage='" + insightMessage + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
