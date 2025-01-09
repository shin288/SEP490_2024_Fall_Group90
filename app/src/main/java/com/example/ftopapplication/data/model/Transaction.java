package com.example.ftopapplication.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Transaction {

    private int transactionId;
    private int transferUserId;  // ID người gửi (sửa từ User senderId)
    private int receiveUserId;   // ID người nhận (sửa từ User receiverId)
    private int transactionAmount; // Số tiền giao dịch (sửa từ amount)
    private String transactionDescription; // Ghi chú giao dịch (sửa từ description)
    private Date transactionDate;  // Ngày giao dịch
    private boolean status;        // Trạng thái giao dịch (thành công/thất bại)

    public Transaction(int transactionId, int transferUserId, int receiveUserId, int transactionAmount, String transactionDescription, Date transactionDate, boolean status) {
        this.transactionId = transactionId;
        this.transferUserId = transferUserId;
        this.receiveUserId = receiveUserId;
        this.transactionAmount = transactionAmount;
        this.transactionDescription = transactionDescription;
        this.transactionDate = transactionDate;
        this.status = status;
    }

    public Transaction(){}

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getTransferUserId() {
        return transferUserId;
    }

    public void setTransferUserId(int transferUserId) {
        this.transferUserId = transferUserId;
    }

    public int getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(int receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public int getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(int transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
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
