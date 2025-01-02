package com.example.ftopapplication.viewmodel.store;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.data.repository.ProductRepository;
import com.example.ftopapplication.data.repository.StoreRepository;
import com.example.ftopapplication.data.repository.VoucherRepository;

public class StoreDetailViewModelFactory implements ViewModelProvider.Factory {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;

    public StoreDetailViewModelFactory(@NonNull StoreRepository storeRepository,
                                       @NonNull ProductRepository productRepository,
                                       @NonNull VoucherRepository voucherRepository) {
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.voucherRepository = voucherRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StoreDetailViewModel.class)) {
            // Safe cast with detailed generic typing
            return modelClass.cast(new StoreDetailViewModel(storeRepository, productRepository, voucherRepository));
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
