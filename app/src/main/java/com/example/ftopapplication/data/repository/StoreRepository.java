package com.example.ftopapplication.data.repository;

import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;
import com.example.ftopapplication.data.model.Store;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreRepository {

    private final ApiService apiService;

    public StoreRepository() {
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    public StoreRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void getStores(final StoreListCallback callback) {
        Call<List<Store>> call = apiService.getStores();
        call.enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new ApiException("Failed to fetch stores, Response Code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getStoreById(int storeId, final StoreDetailCallback callback) {
        Call<Store> call = apiService.getStoreById(storeId);
        call.enqueue(new Callback<Store>() {
            @Override
            public void onResponse(Call<Store> call, Response<Store> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new ApiException("Failed to fetch store details, Response Code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<Store> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface StoreListCallback {
        void onSuccess(List<Store> stores);
        void onError(Throwable throwable);
    }

    public interface StoreDetailCallback {
        void onSuccess(Store store);
        void onError(Throwable throwable);
    }

    public static class ApiException extends Exception {
        public ApiException(String message) {
            super(message);
        }
    }
}
