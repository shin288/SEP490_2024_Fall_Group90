package com.example.ftopapplication.viewmodel.send;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class SendViewModel extends ViewModel {
    private final MutableLiveData<Integer> amount = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> userId = new MutableLiveData<>();


    public LiveData<Integer> getAmount() {
        return amount;
    }

    public void setAmount(int value) {
        amount.setValue(value);
    }

    public LiveData<Integer> getUserId() {
        return userId;
    }

    public void setUserId(int id) {
        userId.setValue(id);
    }

    private MutableLiveData<Integer> userBalance = new MutableLiveData<>();

    public LiveData<Integer> getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(int balance) {
        userBalance.setValue(balance);
    }

    // LiveData kiểm tra số tiền hợp lệ
    public LiveData<Boolean> isAmountValid = Transformations.map(amount, value -> value > 0 && value <= 1_000_000_000);

    // Thêm số tiền (append)
    public void appendAmount(String number) {
        String currentAmount = String.valueOf(amount.getValue());
        if (currentAmount.equals("0")) {
            amount.setValue(Integer.parseInt(number));
        } else if (currentAmount.length() < 9) {
            amount.setValue(Integer.parseInt(currentAmount + number));
        }
    }

    // Xóa chữ số cuối (backspace)
    public void removeLastDigit() {
        String currentAmount = String.valueOf(amount.getValue());
        if (currentAmount.length() > 1) {
            amount.setValue(Integer.parseInt(currentAmount.substring(0, currentAmount.length() - 1)));
        } else {
            amount.setValue(0);
        }
    }
}
