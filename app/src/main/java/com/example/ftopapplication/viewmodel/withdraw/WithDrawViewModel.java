package com.example.ftopapplication.viewmodel.withdraw;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.BankTransfer;
import com.example.ftopapplication.data.repository.BankTransferRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithDrawViewModel extends ViewModel {
    private final MutableLiveData<String> bankName = new MutableLiveData<>();
    private final MutableLiveData<String> accountNumber = new MutableLiveData<>();
    private final MutableLiveData<Integer> amount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final BankTransferRepository bankTransferRepository = new BankTransferRepository();

    public LiveData<String> getBankName() {
        return bankName;
    }

    public void setBankName(String name) {
        bankName.setValue(name);
    }

    public LiveData<String> getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String number) {
        accountNumber.setValue(number);
    }

    public LiveData<Integer> getAmount() {
        return amount;
    }

    public void setAmount(int value) {
        amount.setValue(value);
    }

    public LiveData<Boolean> getIsSuccess() {
        return isSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void performWithdraw(int userId) {
        BankTransfer bankTransfer = new BankTransfer();
        bankTransfer.setWalletUserId(userId);
        bankTransfer.setBankName(bankName.getValue());
        bankTransfer.setAccountNumber(accountNumber.getValue());
        bankTransfer.setTransferAmount(amount.getValue());
        bankTransfer.setTransferType(0); // Type 0 for withdrawal
        bankTransfer.setTransferDescription("User withdrawal");

        bankTransferRepository.withdraw(bankTransfer).enqueue(new Callback<BankTransfer>() {
            @Override
            public void onResponse(Call<BankTransfer> call, Response<BankTransfer> response) {
                if (response.isSuccessful()) {
                    isSuccess.setValue(true);
                } else {
                    isSuccess.setValue(false);
                    errorMessage.setValue("Withdraw failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<BankTransfer> call, Throwable t) {
                isSuccess.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }
}
