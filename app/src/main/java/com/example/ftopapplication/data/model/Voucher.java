package com.example.ftopapplication.data.model;

public class Voucher {
    private int voucherId;           // ID của voucher
    private String voucherName;      // Tên voucher
    private int voucherDiscount;  // Mức giảm giá
    private String expiredDate;      // Ngày hết hạn
    private String createdDate;      // Ngày tạo
    private boolean isDeleted;       // Trạng thái đã xóa
    private Store store;             // Thông tin cửa hàng liên kết

    public Voucher(int voucherId, String voucherName, int voucherDiscount, String expiredDate, String createdDate, boolean isDeleted, Store store) {
        this.voucherId = voucherId;
        this.voucherName = voucherName;
        this.voucherDiscount = voucherDiscount;
        this.expiredDate = expiredDate;
        this.createdDate = createdDate;
        this.isDeleted = isDeleted;
        this.store = store;
    }

    public Voucher() {
    }

    // Getters và Setters
    public int getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(int voucherId) {
        this.voucherId = voucherId;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public int getVoucherDiscount() {
        return voucherDiscount;
    }

    public void setVoucherDiscount(int voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
