package com.example.ftopapplication.viewmodel.topup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.repository.TopUpRepository;

public class TopUpViewModel extends ViewModel {

    private final TopUpRepository repository;
    private final MutableLiveData<String> qrCodeLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public TopUpViewModel() {
        repository = new TopUpRepository();
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
}