package com.example.ftopapplication.viewmodel.topup;

import android.util.Log;

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
    // LiveData để trả về QR Code, TransferId, Trạng thái giao dịch và lỗi
    private final MutableLiveData<String> qrCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> transferIdLiveData = new MutableLiveData<>();
    private final MutableLiveData<BankTransfer> transferStatusLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public TopUpViewModel() {
        repository = new TopUpRepository();
    }

    // Getter cho LiveData
    public LiveData<String> getQrCodeLiveData() {
        return qrCodeLiveData;
    }

    public LiveData<Integer> getTransferIdLiveData() {
        return transferIdLiveData;
    }

    public LiveData<BankTransfer> getTransferStatusLiveData() {
        return transferStatusLiveData;
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
            public void onSuccess(String qrCode, int transferId) {
                qrCodeLiveData.postValue(qrCode);       // Trả về QR Code
                transferIdLiveData.postValue(transferId); // Trả về TransferId để kiểm tra sau
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }
        });
    }

    public void checkTransferStatus(int transferId) {
        repository.checkTransferStatus(transferId, new TopUpRepository.TransferStatusCallback() {
            @Override
            public void onSuccess(BankTransfer transfer) {
                if (transfer != null) {

                    boolean status = transfer.isStatus();
                    if (status) {
                        Log.d("TopUpStatus", "Giao dịch đã thành công.");
                    } else {
                        Log.d("TopUpStatus", "Giao dịch chưa thành công.");
                    }
                    transferStatusLiveData.postValue(transfer);
                }
            }

            @Override
            public void onFailure(String error) {
                errorLiveData.postValue(error);
            }
        });
    }
}