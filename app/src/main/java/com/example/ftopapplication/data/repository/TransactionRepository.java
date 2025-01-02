package com.example.ftopapplication.data.repository;


import com.example.ftopapplication.data.model.ApiResponse;
import com.example.ftopapplication.data.model.OrderTransactionRequest;
import com.example.ftopapplication.data.model.OrderTransactionResponse;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;


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

    // Đặt hàng với giao dịch
    public void placeOrderWithTransaction(OrderTransactionRequest request, TransactionCallback callback) {
        apiService.placeOrderWithTransaction(request).enqueue(new Callback<ApiResponse<OrderTransactionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderTransactionResponse>> call, Response<ApiResponse<OrderTransactionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to process transaction: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderTransactionResponse>> call, Throwable t) {
                callback.onError(t);
            }
        });

    }

    public interface TransactionCallback {
        void onSuccess(ApiResponse<OrderTransactionResponse> response);
        void onError(Throwable throwable);
    }




}
