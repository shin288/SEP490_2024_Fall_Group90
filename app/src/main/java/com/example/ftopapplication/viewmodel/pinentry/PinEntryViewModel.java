package com.example.ftopapplication.viewmodel.pinentry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.ApiResponse;
import com.example.ftopapplication.data.model.OrderTransactionRequest;
import com.example.ftopapplication.data.model.OrderTransactionResponse;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.TransactionRepository;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public void verifyPin(int userId, int  enteredPin, TransactionRepository.UserCallback callback) {
        isLoading.setValue(true);
        transactionRepository.fetchUser(userId, new TransactionRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                if (user.getPin() == (enteredPin)) {
                    callback.onSuccess(user);
                } else {
                    callback.onError(new Throwable("Incorrect PIN"));
                }
            }

            @Override
            public void onError(Throwable throwable) {
                isLoading.setValue(false);
                callback.onError(throwable);
            }
        });
    }

    public void placeOrder(OrderTransactionRequest request, TransactionRepository.OrderTransactionCallback callback) {
        isLoading.setValue(true);
        transactionRepository.placeOrderWithTransaction(request, new TransactionRepository.OrderTransactionCallback() {
            @Override
            public void onSuccess(ApiResponse<OrderTransactionResponse> response) {
                isLoading.setValue(false);
                callback.onSuccess(response);
            }

            @Override
            public void onError(Throwable throwable) {
                isLoading.setValue(false);
                callback.onError(throwable);
            }
        });
    }

    public void transferMoney(int senderUserId, int receiverUserId, int amount, TransactionRepository.SingleTransactionCallback callback) {
        String payload = "{"
                + "\"transferUserId\": " + senderUserId + ","
                + "\"receiveUserId\": " + receiverUserId + ","
                + "\"amount\": " + amount + ","
                + "\"description\": \"Chuyển tiền từ ứng dụng\","
                + "\"status\": false"
                + "}";

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload);
        transactionRepository.transferMoneySingle(body, callback);
    }






}
