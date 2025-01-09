package com.example.ftopapplication.viewmodel.send;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.TransactionRepository;
import com.example.ftopapplication.data.repository.UserRepository;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectContactViewModel extends ViewModel {
    private final MutableLiveData<Integer> senderUserId = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedContactId = new MutableLiveData<>();
    private final MutableLiveData<Integer> amount = new MutableLiveData<>();
    private final MutableLiveData<Boolean> transferSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<User>> users = new MutableLiveData<>();

    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final UserRepository userRepository = new UserRepository();


        private MutableLiveData<Integer> balance = new MutableLiveData<>();

        public LiveData<Integer> getBalance() {
            return balance;
        }

        public void setBalance(int balanceValue) {
            balance.setValue(balanceValue);
        }



    public LiveData<Integer> getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(int id) {
        senderUserId.setValue(id);
    }

    public LiveData<Integer> getSelectedContactId() {
        return selectedContactId;
    }

    public void setSelectedContactId(int id) {
        selectedContactId.setValue(id);
    }

    public LiveData<Integer> getAmount() {
        return amount;
    }

    public void setAmount(int value) {
        amount.setValue(value);
    }

    public LiveData<Boolean> getTransferSuccess() {
        return transferSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    public void fetchUsers() {
        userRepository.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users.setValue(response.body());
                } else {
                    errorMessage.setValue("Unable to fetch user list: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    public void performTransfer(int senderId, int receiverId, int transactionAmount) {
        // Tạo payload JSON thủ công
        String payload = "{"
                + "\"transferUserId\": " + senderId + ","
                + "\"receiveUserId\": " + receiverId + ","
                + "\"amount\": " + transactionAmount + ","
                + "\"description\": \"Transfer via app\"," // Đây là "description" thay vì "transactionDescription"
                + "\"status\": false"
                + "}";

        // Tạo RequestBody từ payload JSON
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), payload);

        // Gọi API qua Retrofit
        transactionRepository.transferMoney(body).enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.isSuccessful()) {
                    // Thành công
                    transferSuccess.setValue(true);
                } else {
                    // Thất bại
                    transferSuccess.setValue(false);
                    errorMessage.setValue("Transfer failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                // Lỗi mạng hoặc kết nối
                transferSuccess.setValue(false);
                errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }


    public void fetchFilteredUsers() {
        userRepository.getFilteredUsers(new UserRepository.UserCallback() {
            @Override
            public void onSuccess(List<User> filteredUsers) {
                users.setValue(filteredUsers);
            }

            @Override
            public void onError(Throwable throwable) {
                errorMessage.setValue(throwable.getMessage());
            }
        });
    }
}
