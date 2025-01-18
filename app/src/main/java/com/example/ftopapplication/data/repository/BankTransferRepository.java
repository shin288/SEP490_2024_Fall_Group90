package com.example.ftopapplication.data.repository;

import com.example.ftopapplication.data.model.BankTransfer;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;

import java.util.List;

import retrofit2.Call;

public class BankTransferRepository {
    private final ApiService apiService;

    public BankTransferRepository() {
        apiService = ApiClient.getApiService();
    }

    public Call<BankTransfer> withdraw(BankTransfer bankTransfer) {
        return apiService.withdraw(bankTransfer);
    }

    public Call<List<BankTransfer>> getAllBankTransfers(int walletUserId) {
        return apiService.getAllBankTransfers(walletUserId);
    }

    public Call<BankTransfer> getTransferById(int transferId){
        return apiService.getTransferById(transferId);
    }

}
