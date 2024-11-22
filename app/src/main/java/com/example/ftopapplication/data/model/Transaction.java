package com.example.ftopapplication.data.model;

import java.util.Date;

public class Transaction {
    private int transactionId;
    private int senderId;          // ID người gửi
    private int receiverId;        // ID người nhận
    private float amount;          // Số tiền giao dịch
    private String description;    // Ghi chú giao dịch
    private Date transactionDate;  // Ngày giao dịch
    private boolean status;        // Trạng thái giao dịch (thành công/thất bại)

    // Constructor
    public Transaction(int transactionId, int senderId, int receiverId, float amount, String description, Date transactionDate, boolean status) {
        this.transactionId = transactionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.status = status;
    }

    // Getters và Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
