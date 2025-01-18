package com.example.ftopapplication.data.repository;

import com.example.ftopapplication.data.model.BankTransfer;
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
        void onSuccess(String qrCode, int transferId);
        void onError(String error);
    }

    public void topUp(int walletUserId, int amount, TopUpCallback callback) {
        TopUpRequest request = new TopUpRequest(walletUserId, amount);
        apiService.topUp(request).enqueue(new Callback<TopUpResponse>() {
            @Override
            public void onResponse(Call<TopUpResponse> call, Response<TopUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int transferId = response.body().getBankTransfer().getTransferId();
                    String qrCode = response.body().getPaymentLink().getQrCode();
                    callback.onSuccess(qrCode, transferId);
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

    // Interface callback để xử lý kết quả kiểm tra trạng thái
    public interface TransferStatusCallback {
        void onSuccess(BankTransfer transfer);
        void onFailure(String error);
    }

    // 4️⃣ API kiểm tra trạng thái giao dịch theo TransferId
    public void checkTransferStatus(int transferId, TransferStatusCallback callback) {
        apiService.getTransferById(transferId).enqueue(new Callback<BankTransfer>() {
            @Override
            public void onResponse(Call<BankTransfer> call, Response<BankTransfer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());  // Trả về kết quả giao dịch
                } else {
                    callback.onFailure("Không tìm thấy giao dịch.");
                }
            }

            @Override
            public void onFailure(Call<BankTransfer> call, Throwable t) {
                callback.onFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

}
