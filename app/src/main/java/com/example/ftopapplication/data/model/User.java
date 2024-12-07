package com.example.ftopapplication.data.model;

public class User {
    private int id;
    private String email;
    private String displayName;
    private String avatar;
    private String phoneNumber;
    private String role;
    private String password;
    private int pin;
    private int walletBalance;
    private boolean isActive;


    // Constructor


    public User(int id, String email, String displayName, String avatar, String phoneNumber, String role, String password, int pin, int walletBalance, boolean isActive) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.avatar = avatar;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.password = password;
        this.pin = pin;
        this.walletBalance = walletBalance;
        this.isActive = isActive;
    }

    public User() {

    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(int walletBalance) {
        this.walletBalance = walletBalance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}