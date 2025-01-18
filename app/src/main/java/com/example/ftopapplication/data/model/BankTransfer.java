package com.example.ftopapplication.data.model;

import java.time.LocalDateTime;
import java.util.Date;

public class BankTransfer {
    private int transferId;
    private int walletUserId;
    private int accountNumber;
    private String bankName;
    private boolean transferType; // 1 = Deposit, 0 = Withdraw
    private String transferDescription;
    private Date transferDate;
    private boolean status;
    private int transferAmount;

    // Getters and Setters
    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getWalletUserId() {
        return walletUserId;
    }

    public void setWalletUserId(int walletUserId) {
        this.walletUserId = walletUserId;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public boolean getTransferType() {
        return transferType;
    }

    public void setTransferType(boolean transferType) {
        this.transferType = transferType;
    }

    public String getTransferDescription() {
        return transferDescription;
    }

    public void setTransferDescription(String transferDescription) {
        this.transferDescription = transferDescription;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setAmount(Integer amount) {
        this.transferAmount = amount;
    }

    public Integer getAmount() {
        return transferAmount;
    }


    @Override
    public String toString() {
        return "BankTransfer{" +
                "transferId=" + transferId +
                ", walletUserId=" + walletUserId +
                ", accountNumber=" + accountNumber +
                ", bankName='" + bankName + '\'' +
                ", transferType=" + transferType +
                ", transferDescription='" + transferDescription + '\'' +
                ", transferDate='" + transferDate + '\'' +
                ", status=" + status +
                ", transferAmount=" + transferAmount +
                '}';
    }
}
