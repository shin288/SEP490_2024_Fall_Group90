package com.example.ftopapplication.data.repository;

import com.example.ftopapplication.data.model.TopUpRequest;
import com.example.ftopapplication.data.model.TopUpResponse;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUpRepository {

    private final ApiService apiService;

    public TopUpRepository() {
        apiService = ApiClient.getApiService();
    }

    public interface TopUpCallback {
        void onSuccess(String qrCode);
        void onError(String error);
    }

    public void topUp(int walletUserId, int amount, TopUpCallback callback) {
        TopUpRequest request = new TopUpRequest(walletUserId, amount);
        apiService.topUp(request).enqueue(new Callback<TopUpResponse>() {
            @Override
            public void onResponse(Call<TopUpResponse> call, Response<TopUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getPaymentLink().getQrCode());
                } else {
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<TopUpResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

}
