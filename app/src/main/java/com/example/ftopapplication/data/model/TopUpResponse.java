package com.example.ftopapplication.data.model;

import com.google.gson.annotations.SerializedName;

public class TopUpResponse {

    @SerializedName("bankTransfer")  // Thêm trường bankTransfer
    private BankTransfer bankTransfer;

    @SerializedName("walletBalance")
    private int walletBalance;

    @SerializedName("pin")
    private int pin;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("paymentLink")
    private PaymentLink paymentLink;

    public int getWalletBalance() {
        return walletBalance;
    }

    public int getPin() {
        return pin;
    }

    public boolean isActive() {
        return isActive;
    }

    public PaymentLink getPaymentLink() {
        return paymentLink;
    }

    // Getter cho bankTransfer
    public BankTransfer getBankTransfer() {
        return bankTransfer;
    }

    public static class PaymentLink {
        @SerializedName("qrCode")
        private String qrCode;

        @SerializedName("checkoutUrl")
        private String checkoutUrl;

        public String getQrCode() {
            return qrCode;
        }

        public String getCheckoutUrl() {
            return checkoutUrl;
        }
    }
}
