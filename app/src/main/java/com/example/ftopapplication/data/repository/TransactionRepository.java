package com.example.ftopapplication.data.repository;


import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public class TransactionRepository {
    private ApiService apiService;

    public TransactionRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    // Tạo giao dịch
    public Call<Void> createTransaction(Transaction transaction) {
        return apiService.createTransaction(transaction);
    }

    // Thực hiện chuyển tiền
    public Call<Transaction> transferMoney(Transaction transaction) {
        return apiService.transferMoney(transaction);
    }

    public Call<List<Transaction>> getAllTransactionsForUser(int userId) {
        return apiService.getAllTransactionsForUser(userId);
    }


}
