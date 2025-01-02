package com.example.ftopapplication.data.model;

import java.util.List;

public class OrderTransactionResponse {
    private Order order;
    private List<OrderItem> orderItems;
    private Transaction transaction;

    // Getters và Setters
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
