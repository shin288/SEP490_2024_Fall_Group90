package com.example.ftopapplication.data.repository;

import com.example.ftopapplication.data.model.Product;
import com.example.ftopapplication.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {

    private final ApiService apiService;

    public ProductRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    // Lấy danh sách sản phẩm theo storeId
    public void getProductsByStoreId(int storeId, final ProductCallback callback) {
        Call<List<Product>> call = apiService.getProductsByStoreId(storeId);
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleApiError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                handleNetworkError(t, callback);
            }
        });
    }

    private void handleApiError(Response<?> response, ProductCallback callback) {
        String errorMessage = "Lỗi tải sản phẩm. Mã lỗi: " + response.code();
        if (response.errorBody() != null) {
            try {
                errorMessage += " - " + response.errorBody().string();
            } catch (Exception e) {
                errorMessage += " - Không thể đọc chi tiết lỗi.";
            }
        }
        callback.onError(new ApiException(errorMessage));
    }

    private void handleNetworkError(Throwable t, ProductCallback callback) {
        callback.onError(new ApiException("Lỗi kết nối mạng: " + t.getMessage()));
    }

    // Giao diện Callback cho danh sách sản phẩm
    public interface ProductCallback {
        void onSuccess(List<Product> products);
        void onError(Throwable throwable);
    }

    // Lớp ApiException tùy chỉnh để xác định lỗi
    public static class ApiException extends Exception {
        public ApiException(String message) {
            super(message);
        }
    }
}
