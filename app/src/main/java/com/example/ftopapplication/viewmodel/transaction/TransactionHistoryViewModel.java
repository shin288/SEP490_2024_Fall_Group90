package com.example.ftopapplication.viewmodel.transaction;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.BankTransfer;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.repository.BankTransferRepository;
import com.example.ftopapplication.data.repository.TransactionRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionHistoryViewModel extends ViewModel {

    private final MutableLiveData<List<Object>> transactionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final TransactionRepository transactionRepository;
    private final BankTransferRepository bankTransferRepository;

    private List<Object> allTransactions = new ArrayList<>();

    public TransactionHistoryViewModel() {
        transactionRepository = new TransactionRepository();
        bankTransferRepository = new BankTransferRepository();
    }

    public LiveData<List<Object>> getTransactionsLiveData() {
        return transactionsLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchAllTransactions(int userId) {
        transactionRepository.fetchTransactions(userId, new TransactionRepository.TransactionCallback() {
            @Override
            public void onSuccess(List<Transaction> transactions) {
                bankTransferRepository.getAllBankTransfers(userId).enqueue(new retrofit2.Callback<List<BankTransfer>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<BankTransfer>> call, retrofit2.Response<List<BankTransfer>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<BankTransfer> transfers = response.body();
                            List<Object> combinedTransactions = mergeTransactions(transactions, transfers);
                            allTransactions = combinedTransactions;
                            transactionsLiveData.postValue(allTransactions);
                        } else {
                            errorLiveData.postValue("Failed to fetch bank transfers");
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<BankTransfer>> call, Throwable t) {
                        errorLiveData.postValue("Error: " + t.getMessage());
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                errorLiveData.postValue("Failed to fetch transactions: " + throwable.getMessage());
            }
        });
    }

    private List<Object> mergeTransactions(List<Transaction> transactions, List<BankTransfer> transfers) {
        List<Object> combined = new ArrayList<>();
        combined.addAll(transactions);
        combined.addAll(transfers);

        combined.sort((o1, o2) -> {
            if (o1 instanceof Transaction && o2 instanceof Transaction) {
                return ((Transaction) o2).getTransactionDate().compareTo(((Transaction) o1).getTransactionDate());
            } else if (o1 instanceof BankTransfer && o2 instanceof BankTransfer) {
                return ((BankTransfer) o2).getTransferDate().compareTo(((BankTransfer) o1).getTransferDate());
            } else if (o1 instanceof Transaction && o2 instanceof BankTransfer) {
                return ((BankTransfer) o2).getTransferDate().compareTo(((Transaction) o1).getTransactionDate());
            } else {
                return ((Transaction) o2).getTransactionDate().compareTo(((BankTransfer) o1).getTransferDate());
            }
        });

        return combined;
    }



    public void filterTransactions(String filterType, int userId) {
        if (allTransactions == null || allTransactions.isEmpty()) return;

        List<Object> filteredTransactions;

        if ("income".equalsIgnoreCase(filterType)) {
            filteredTransactions = allTransactions.stream()
                    .filter(item -> {
                        if (item instanceof Transaction) {
                            Transaction t = (Transaction) item;
                            return t.getReceiveUserId() == userId;
                        } else if (item instanceof BankTransfer) {
                            BankTransfer b = (BankTransfer) item;
                            return b.getTransferType() == true;  // Top-up
                        }
                        return false;
                    })
                    .limit(10)
                    .collect(Collectors.toList());

        } else if ("expense".equalsIgnoreCase(filterType)) {
            filteredTransactions = allTransactions.stream()
                    .filter(item -> {
                        if (item instanceof Transaction) {
                            Transaction t = (Transaction) item;
                            return t.getTransferUserId() == userId;
                        } else if (item instanceof BankTransfer) {
                            BankTransfer b = (BankTransfer) item;
                            return b.getTransferType() == false;  // Withdraw
                        }
                        return false;
                    })
                    .limit(10)
                    .collect(Collectors.toList());

        } else {
            filteredTransactions = allTransactions.stream()
                    .limit(20)
                    .collect(Collectors.toList());
        }

        transactionsLiveData.postValue(filteredTransactions);
    }
}
