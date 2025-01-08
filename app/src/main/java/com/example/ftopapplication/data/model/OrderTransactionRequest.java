package com.example.ftopapplication.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class OrderTransactionRequest implements Parcelable {
    private int userId;
    private int storeId;
    private Integer voucherId; // Nullable
    private List<ProductOrder> products;
    private String note;
    private double totalPrice;

    public OrderTransactionRequest(int userId, int storeId, Integer voucherId, List<ProductOrder> products, String note, double totalPrice) {
        this.userId = userId;
        this.storeId = storeId;
        this.voucherId = voucherId;
        this.products = products;
        this.note = note;
        this.totalPrice = totalPrice;
    }

    // Parcelable Implementation
    protected OrderTransactionRequest(Parcel in) {
        userId = in.readInt();
        storeId = in.readInt();
        if (in.readByte() == 0) {
            voucherId = null;
        } else {
            voucherId = in.readInt();
        }
        products = in.createTypedArrayList(ProductOrder.CREATOR);
        note = in.readString();
        totalPrice = in.readDouble();
    }

    public static final Creator<OrderTransactionRequest> CREATOR = new Creator<OrderTransactionRequest>() {
        @Override
        public OrderTransactionRequest createFromParcel(Parcel in) {
            return new OrderTransactionRequest(in);
        }

        @Override
        public OrderTransactionRequest[] newArray(int size) {
            return new OrderTransactionRequest[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeInt(storeId);
        if (voucherId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(voucherId);
        }
        dest.writeTypedList(products);
        dest.writeString(note);
        dest.writeDouble(totalPrice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters và Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public List<ProductOrder> getProducts() {
        return products;
    }

    public void setProducts(List<ProductOrder> products) {
        this.products = products;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Convenience method to add a ProductOrder
    public void addProductOrder(ProductOrder productOrder) {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        this.products.add(productOrder);
    }

    @Override
    public String toString() {
        return "OrderTransactionRequest{" +
                "userId=" + userId +
                ", storeId=" + storeId +
                ", voucherId=" + voucherId +
                ", products=" + products +
                ", note='" + note + '\'' +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
