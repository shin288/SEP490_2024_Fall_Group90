package com.example.ftopapplication.viewmodel.pinentry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.ApiResponse;
import com.example.ftopapplication.data.model.OrderTransactionRequest;
import com.example.ftopapplication.data.model.OrderTransactionResponse;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.repository.TransactionRepository;

public class PinEntryViewModel extends ViewModel {
    private final TransactionRepository transactionRepository;

    private final MutableLiveData<ApiResponse<OrderTransactionResponse>> orderResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Transaction> transferResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public PinEntryViewModel(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public LiveData<ApiResponse<OrderTransactionResponse>> getOrderResponseLiveData() {
        return orderResponseLiveData;
    }

    public LiveData<Transaction> getTransferResponseLiveData() {
        return transferResponseLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Gọi API placeOrderWithTransaction
    public void placeOrder(OrderTransactionRequest request) {
        isLoading.setValue(true);
        transactionRepository.placeOrderWithTransaction(request, new TransactionRepository.OrderTransactionCallback() {
            @Override
            public void onSuccess(ApiResponse<OrderTransactionResponse> response) {
                isLoading.setValue(false);
                orderResponseLiveData.setValue(response);
            }

            @Override
            public void onError(Throwable throwable) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to process the order: " + throwable.getMessage());
            }
        });
    }

    // Gọi API transferMoney
    public void transferMoney(Transaction transaction) {
        isLoading.setValue(true);
        transactionRepository.transferMoney(transaction).enqueue(new retrofit2.Callback<Transaction>() {
            @Override
            public void onResponse(retrofit2.Call<Transaction> call, retrofit2.Response<Transaction> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    transferResponseLiveData.setValue(response.body());
                } else {
                    errorMessage.setValue("Transfer failed: " + response.message());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Transaction> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
