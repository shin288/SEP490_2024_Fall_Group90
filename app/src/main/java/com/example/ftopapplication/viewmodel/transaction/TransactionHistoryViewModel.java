package com.example.ftopapplication.viewmodel.transaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.repository.TransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionHistoryViewModel extends ViewModel {
    private final MutableLiveData<List<Transaction>> transactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final TransactionRepository repository;

    private List<Transaction> allTransactions; // Lưu toàn bộ danh sách giao dịch

    public TransactionHistoryViewModel() {
        repository = new TransactionRepository();
    }

    public LiveData<List<Transaction>> getTransactionsLiveData() {
        return transactionsLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Lấy tất cả giao dịch từ repository
    public void fetchAllTransactions(int userId) {
        repository.fetchTransactions(userId, new TransactionRepository.TransactionCallback() {
            @Override
            public void onSuccess(List<Transaction> transactions) {
                // Lưu tất cả giao dịch để lọc sau
                allTransactions = transactions.stream()
                        .filter(transaction -> transaction.getReceiveUserId() == userId || transaction.getTransferUserId() == userId)
                        .collect(Collectors.toList());
                transactionsLiveData.postValue(allTransactions);
            }

            @Override
            public void onError(Throwable throwable) {
                errorLiveData.postValue("Failed to fetch transactions: " + throwable.getMessage());
            }
        });
    }

    // Lọc giao dịch dựa trên loại (income, expense, all)
    public void filterTransactions(String filterType, int userId) {
        if (allTransactions == null) return;

        if ("income".equals(filterType)) {
            transactionsLiveData.postValue(filterByType(true, userId));
        } else if ("expense".equals(filterType)) {
            transactionsLiveData.postValue(filterByType(false, userId));
        } else {
            transactionsLiveData.postValue(allTransactions); // Hiển thị tất cả
        }
    }

    // Lọc giao dịch dựa trên `receiveUserId` (income) hoặc `transferUserId` (expense)
    private List<Transaction> filterByType(boolean isIncome, int userId) {
        return allTransactions.stream()
                .filter(transaction -> isIncome
                        ? transaction.getReceiveUserId() == userId // Income: receiverId = userId
                        : transaction.getTransferUserId() == userId) // Expense: transferUserId = userId
                .collect(Collectors.toList());
    }
}
