package com.example.ftopapplication.data.model;

public class TopUpRequest {
    private int walletUserId;
    private int amount;

    public TopUpRequest(int walletUserId, int amount) {
        this.walletUserId = walletUserId;
        this.amount = amount;
    }

    public int getWalletUserId() {
        return walletUserId;
    }

    public int getAmount() {
        return amount;
    }
}
