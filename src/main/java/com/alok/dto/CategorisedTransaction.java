package com.alok.dto;

@SuppressWarnings("unused")
public class CategorisedTransaction {
    private String transactionId;
    private String customerId;
    private String category;
    private String merchantCode;
    private String merchantName;
    private String transactionDesc;
    private double amount;
    private String amountType;
    private Long timestamp;

    public CategorisedTransaction() {}

    public CategorisedTransaction(String transactionId, String customerId, String category, String merchantCode, String merchantName, String transactionDesc, double amount, String amountType, Long timestamp) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.category = category;
        this.merchantCode = merchantCode;
        this.merchantName = merchantName;
        this.transactionDesc = transactionDesc;
        this.amount = amount;
        this.amountType = amountType;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getTransactionDesc() {
        return transactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        this.transactionDesc = transactionDesc;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAmountType() {
        return amountType;
    }

    public void setAmountType(String amountType) {
        this.amountType = amountType;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}


