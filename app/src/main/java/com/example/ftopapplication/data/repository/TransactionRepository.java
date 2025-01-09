package com.example.ftopapplication.data.repository;


import com.example.ftopapplication.data.model.ApiResponse;
import com.example.ftopapplication.data.model.OrderTransactionRequest;
import com.example.ftopapplication.data.model.OrderTransactionResponse;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.model.TransactionSummary;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;


public class TransactionRepository {
    private ApiService apiService;

    public TransactionRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    public TransactionRepository(ApiService apiService) {
        this.apiService = apiService;
    }
    // Tạo giao dịch
    public Call<Void> createTransaction(Transaction transaction) {
        return apiService.createTransaction(transaction);
    }

    // Thực hiện chuyển tiền
    public Call<Transaction> transferMoney(Transaction transaction) {
        return apiService.transferMoney(transaction);
    }




    // Đặt hàng với giao dịch
    public void placeOrderWithTransaction(OrderTransactionRequest request, OrderTransactionCallback callback) {
        apiService.placeOrderWithTransaction(request).enqueue(new Callback<ApiResponse<OrderTransactionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderTransactionResponse>> call, Response<ApiResponse<OrderTransactionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else if (response.errorBody() != null) {
                    try {
                        String error = response.errorBody().string();
                        ApiResponse errorResponse = new Gson().fromJson(error, ApiResponse.class);
                        callback.onError(new Throwable(errorResponse.getMessage()));
                    } catch (IOException e) {
                        callback.onError(new Throwable("Failed to process transaction."));
                    }
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


    public void fetchTransactions(int userId, TransactionCallback callback) {
        apiService.getAllTransactionsForUser(userId).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to fetch transactions: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void fetchTransactionSummary(int userId, TransactionSummaryCallback callback) {
        apiService.getTransactionSummary(userId).enqueue(new Callback<TransactionSummary>() {
            @Override
            public void onResponse(Call<TransactionSummary> call, Response<TransactionSummary> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to fetch transaction summary: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<TransactionSummary> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void fetchUser(int userId, UserCallback callback) {
        apiService.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("Failed to fetch user data."));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void transferMoneySingle(Transaction transaction, SingleTransactionCallback callback) {
        apiService.transferMoney(transaction).enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body()); // Gửi đối tượng Transaction đến callback
                } else if (response.code() == 400) {
                    callback.onError(new Throwable("Insufficient balance"));
                } else {
                    callback.onError(new Throwable("Transfer failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                callback.onError(t);
            }
        });
    }


    public interface TransactionSummaryCallback {
        void onSuccess(TransactionSummary summary);
        void onError(Throwable throwable);
    }



    public interface OrderTransactionCallback {
        void onSuccess(ApiResponse<OrderTransactionResponse> response);
        void onError(Throwable throwable);
    }

    public interface TransactionCallback {
        void onSuccess(List<Transaction> transactions);
        void onError(Throwable throwable);
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onError(Throwable throwable);
    }

    public interface SingleTransactionCallback {
        void onSuccess(Transaction transaction);
        void onError(Throwable throwable);
    }

}
