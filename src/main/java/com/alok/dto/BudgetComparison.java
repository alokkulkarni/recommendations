package com.alok.dto;

public class BudgetComparison {

    private String customerId;
    private String category;
    private int month;
    private int year;
    private double spentAmount;
    private double budget;
    private String flag; // Values: "OVERSPENT", "UNDERSPENT"

    public BudgetComparison() {}

    public BudgetComparison(String customerId, String category, int month, int year,
                            double spentAmount, double budget, String flag) {
        this.customerId = customerId;
        this.category = category;
        this.month = month;
        this.year = year;
        this.spentAmount = spentAmount;
        this.budget = budget;
        this.flag = flag;
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

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
