package com.example.ftopapplication.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private int productId;
    private String productName;
    private int productPrice;
    private int categoryId;
    private boolean status;
    private String productImage;
    private int storeId;

    // Constructor
    public Product(int productId, String productName, int productPrice, int categoryId, boolean status, String productImage, int storeId) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.categoryId = categoryId;
        this.status = status;
        this.productImage = productImage;
        this.storeId = storeId;
    }

    // Parcelable implementation
    protected Product(Parcel in) {
        productId = in.readInt();
        productName = in.readString();
        productPrice = in.readInt();
        categoryId = in.readInt();
        status = in.readByte() != 0;
        productImage = in.readString();
        storeId = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeString(productName);
        dest.writeInt(productPrice);
        dest.writeInt(categoryId);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeString(productImage);
        dest.writeInt(storeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters và Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }
}
