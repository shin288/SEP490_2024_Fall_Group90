package com.example.ftopapplication.data.repository;

import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;

import retrofit2.Call;

public class TransactionRepository {
    private ApiService apiService;

    public TransactionRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    // Tạo giao dịch
    public Call<Void> createTransaction(Transaction transaction) {
        return apiService.createTransaction(transaction);
    }
}
