package com.example.ftopapplication.viewmodel.topup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.BankTransfer;
import com.example.ftopapplication.data.repository.BankTransferRepository;
import com.example.ftopapplication.data.repository.TopUpRepository;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUpViewModel extends ViewModel {

    private final TopUpRepository repository;
    private final BankTransferRepository bankTransferRepository;
    private final MutableLiveData<String> qrCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public TopUpViewModel() {
        repository = new TopUpRepository();
        bankTransferRepository = new BankTransferRepository();
    }

    public LiveData<String> getQrCodeLiveData() {
        return qrCodeLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void topUp(int walletUserId, int amount) {
        if (walletUserId <= 0 || amount <= 0) {
            errorLiveData.postValue("Invalid walletUserId or amount.");
            return;
        }

        repository.topUp(walletUserId, amount, new TopUpRepository.TopUpCallback() {
            @Override
            public void onSuccess(String qrCode) {
                qrCodeLiveData.postValue(qrCode);
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }

    private final MutableLiveData<List<BankTransfer>> bankTransfersLiveData = new MutableLiveData<>();

    public LiveData<List<BankTransfer>> getBankTransfersLiveData() {
        return bankTransfersLiveData;
    }

    // Lọc danh sách giao dịch theo userId
    public void fetchBankTransfersByUserId(int userId) {
        bankTransferRepository.getAllBankTransfers().enqueue(new Callback<List<BankTransfer>>() {
            @Override
            public void onResponse(Call<List<BankTransfer>> call, Response<List<BankTransfer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lọc giao dịch theo walletUserId
                    List<BankTransfer> filteredTransfers = response.body().stream()
                            .filter(transfer -> transfer.getWalletUserId() == userId)
                            .collect(Collectors.toList());
                    bankTransfersLiveData.postValue(filteredTransfers);
                } else {
                    errorLiveData.postValue("Không thể lấy dữ liệu giao dịch.");
                }
            }

            @Override
            public void onFailure(Call<List<BankTransfer>> call, Throwable t) {
                errorLiveData.postValue("Lỗi: " + t.getMessage());
            }
        });
    }
}