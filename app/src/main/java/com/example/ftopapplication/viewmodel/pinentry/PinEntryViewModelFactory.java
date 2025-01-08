package com.example.ftopapplication.viewmodel.pinentry;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.data.repository.TransactionRepository;

public class PinEntryViewModelFactory implements ViewModelProvider.Factory {
    private final TransactionRepository transactionRepository;

    public PinEntryViewModelFactory(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PinEntryViewModel.class)) {
            return (T) new PinEntryViewModel(transactionRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
