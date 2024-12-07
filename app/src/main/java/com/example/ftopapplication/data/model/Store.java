package com.example.ftopapplication.data.model;

import java.util.List;

public class Store {
    private int storeId;
    private String storeName;
    private String storeAddress;
    private String storePhone;
    private boolean status;
    private List<String> storeImage;


    public Store(int storeId, String storeAddress, String storeName, String storePhone, boolean status, List<String> storeImage) {
        this.storeId = storeId;
        this.storeAddress = storeAddress;
        this.storeName = storeName;
        this.storePhone = storePhone;
        this.status = status;
        this.storeImage = storeImage;
    }

    public Store() {
    }

    public List<String> getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(List<String> storeImage) {
        this.storeImage = storeImage;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


}
