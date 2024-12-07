package com.example.ftopapplication.data.repository;

import com.example.ftopapplication.data.model.Voucher;
import com.example.ftopapplication.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherRepository {

    private final ApiService apiService;

    public VoucherRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    // Lấy tất cả voucher
    public void getVouchers(final VoucherCallback callback) {
        Call<List<Voucher>> call = apiService.getVouchers();
        call.enqueue(new Callback<List<Voucher>>() {
            @Override
            public void onResponse(Call<List<Voucher>> call, Response<List<Voucher>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleApiError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<List<Voucher>> call, Throwable t) {
                handleNetworkError(t, callback);
            }
        });
    }

    // Lấy danh sách voucher theo storeId
    public void getVouchersByStoreId(int storeId, final VoucherCallback callback) {
        Call<List<Voucher>> call = apiService.getVouchersByStoreId(storeId);
        call.enqueue(new Callback<List<Voucher>>() {
            @Override
            public void onResponse(Call<List<Voucher>> call, Response<List<Voucher>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    handleApiError(response, callback);
                }
            }

            @Override
            public void onFailure(Call<List<Voucher>> call, Throwable t) {
                handleNetworkError(t, callback);
            }
        });
    }

    private void handleApiError(Response<?> response, VoucherCallback callback) {
        String errorMessage = "Lỗi tải voucher. Mã lỗi: " + response.code();
        if (response.errorBody() != null) {
            try {
                errorMessage += " - " + response.errorBody().string();
            } catch (Exception e) {
                errorMessage += " - Không thể đọc chi tiết lỗi.";
            }
        }
        callback.onError(new ApiException(errorMessage));
    }

    private void handleNetworkError(Throwable t, VoucherCallback callback) {
        callback.onError(new ApiException("Lỗi kết nối mạng: " + t.getMessage()));
    }

    public interface VoucherCallback {
        void onSuccess(List<Voucher> vouchers);
        void onError(Throwable throwable);
    }

    public static class ApiException extends Exception {
        public ApiException(String message) {
            super(message);
        }
    }
}
