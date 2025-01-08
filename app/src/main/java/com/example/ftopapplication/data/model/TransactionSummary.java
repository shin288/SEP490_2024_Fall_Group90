package com.example.ftopapplication.data.model;

import java.util.List;

public class TransactionSummary {
    private int income; // Tổng số tiền nhận được
    private int expense; // Tổng số tiền đã chuyển đi
    private int balance; // Số dư hiện tại
    private List<Transaction> transactions; // Danh sách giao dịch

    // Getters và Setters
    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getExpense() {
        return expense;
    }

    public void setExpense(int expense) {
        this.expense = expense;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
