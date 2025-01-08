package com.example.ftopapplication.viewmodel.transaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.model.TransactionSummary;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.TransactionRepository;

import java.util.List;

public class StaticViewModel extends ViewModel {
    private final MutableLiveData<List<Transaction>> transactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> incomeLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> expenseLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> balanceLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final TransactionRepository repository;

    public StaticViewModel() {
        repository = new TransactionRepository();
    }

    // Getter for transactions
    public LiveData<List<Transaction>> getTransactionsLiveData() {
        return transactionsLiveData;
    }

    // Getter for income
    public LiveData<Integer> getIncomeLiveData() {
        return incomeLiveData;
    }

    // Getter for expense
    public LiveData<Integer> getExpenseLiveData() {
        return expenseLiveData;
    }

    // Getter for balance
    public LiveData<Integer> getBalanceLiveData() {
        return balanceLiveData;
    }

    // Getter for errors
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Fetch transaction summary (income, expense, balance)
    public void fetchTransactionSummary(int userId) {
        repository.fetchTransactionSummary(userId, new TransactionRepository.TransactionSummaryCallback() {
            @Override
            public void onSuccess(TransactionSummary summary) {
                incomeLiveData.postValue(summary.getIncome());
                expenseLiveData.postValue(summary.getExpense());
                balanceLiveData.postValue(summary.getBalance());
                transactionsLiveData.postValue(summary.getTransactions());
            }

            @Override
            public void onError(Throwable throwable) {
                errorLiveData.postValue(throwable.getMessage());
            }
        });
    }

    // Fetch all transactions (if needed separately)
    public void fetchTransactions(int userId) {
        repository.fetchTransactions(userId, new TransactionRepository.TransactionCallback() {
            @Override
            public void onSuccess(List<Transaction> transactions) {
                transactionsLiveData.postValue(transactions);
            }

            @Override
            public void onError(Throwable throwable) {
                errorLiveData.postValue(throwable.getMessage());
            }
        });
    }

    public void fetchUserWalletBalance(int userId) {
        repository.fetchUser(userId, new TransactionRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                balanceLiveData.postValue(user.getWalletBalance()); // Lấy walletBalance từ User
            }

            @Override
            public void onError(Throwable throwable) {
                errorLiveData.postValue("Failed to fetch user wallet balance: " + throwable.getMessage());
            }
        });
    }

}
