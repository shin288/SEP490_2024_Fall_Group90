package com.example.ftopapplication.data.model;

import java.util.Date;

public class Transaction {
    private int transactionId;
    private User senderId;          // ID người gửi
    private User receiverId;        // ID người nhận
    private float amount;          // Số tiền giao dịch
    private String description;    // Ghi chú giao dịch
    private Date transactionDate;  // Ngày giao dịch
    private boolean status;        // Trạng thái giao dịch (thành công/thất bại)

    public Transaction(int transactionId, User senderId, User receiverId, float amount, String description, Date transactionDate, boolean status) {
        this.transactionId = transactionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
        this.status = status;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public User getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(User receiverId) {
        this.receiverId = receiverId;
    }

    public User getSenderId() {
        return senderId;
    }

    public void setSenderId(User senderId) {
        this.senderId = senderId;
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