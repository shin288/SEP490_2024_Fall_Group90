package com.example.ftopapplication.viewmodel.store;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.adapter.ProductAdapter;
import com.example.ftopapplication.adapter.VoucherAdapter;
import com.example.ftopapplication.data.model.ApiResponse;
import com.example.ftopapplication.data.model.OrderTransactionRequest;
import com.example.ftopapplication.data.model.OrderTransactionResponse;
import com.example.ftopapplication.data.model.Product;
import com.example.ftopapplication.data.model.Store;
import com.example.ftopapplication.data.model.Voucher;
import com.example.ftopapplication.data.repository.ProductRepository;
import com.example.ftopapplication.data.repository.StoreRepository;
import com.example.ftopapplication.data.repository.TransactionRepository;
import com.example.ftopapplication.data.repository.VoucherRepository;

import java.util.List;

public class StoreDetailViewModel extends ViewModel {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;
    private final TransactionRepository transactionRepository;


    private final MutableLiveData<ApiResponse<OrderTransactionResponse>> orderTransactionLiveData = new MutableLiveData<>();

    private final MutableLiveData<Store> storeLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> productLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Voucher>> voucherLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final MutableLiveData<Boolean> isStoreLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isProductLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isVoucherLoading = new MutableLiveData<>(false);

    // Khai báo adapter
    private ProductAdapter productAdapter;
    private VoucherAdapter voucherAdapter;

    // Setter để truyền adapter từ Activity
    public void setProductAdapter(ProductAdapter productAdapter) {
        this.productAdapter = productAdapter;
    }

    public void setVoucherAdapter(VoucherAdapter voucherAdapter) {
        this.voucherAdapter = voucherAdapter;
    }

    public StoreDetailViewModel(StoreRepository storeRepository, ProductRepository productRepository, VoucherRepository voucherRepository, TransactionRepository transactionRepository) {
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.voucherRepository = voucherRepository;
        this.transactionRepository = transactionRepository;
    }

    public LiveData<Store> getStoreLiveData() {
        return storeLiveData;
    }

    public LiveData<List<Product>> getProductLiveData() {
        return productLiveData;
    }

    public LiveData<List<Voucher>> getVoucherLiveData() {
        return voucherLiveData;
    }

    public LiveData<ApiResponse<OrderTransactionResponse>> getOrderTransactionLiveData() {
        return orderTransactionLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsStoreLoading() {
        return isStoreLoading;
    }

    public LiveData<Boolean> getIsProductLoading() {
        return isProductLoading;
    }

    public LiveData<Boolean> getIsVoucherLoading() {
        return isVoucherLoading;
    }

    public void fetchAllData(int storeId) {
        isLoading.setValue(true);

        // Load store details
        isStoreLoading.setValue(true);
        storeRepository.getStoreById(storeId, new StoreRepository.StoreDetailCallback() {
            @Override
            public void onSuccess(Store store) {
                storeLiveData.setValue(store);
                isStoreLoading.setValue(false);
                checkLoadingComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                errorMessage.setValue("Cannot load store details: " + throwable.getMessage());
                isStoreLoading.setValue(false);
                checkLoadingComplete();
            }
        });

        // Load products
        isProductLoading.setValue(true);
        productRepository.getProductsByStoreId(storeId, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productLiveData.setValue(products);
                isProductLoading.setValue(false);
                checkLoadingComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                errorMessage.setValue("Cannot load products: " + throwable.getMessage());
                isProductLoading.setValue(false);
                checkLoadingComplete();
            }
        });

        // Load vouchers
        isVoucherLoading.setValue(true);
        voucherRepository.getVouchersByStoreId(storeId, new VoucherRepository.VoucherCallback() {
            @Override
            public void onSuccess(List<Voucher> vouchers) {
                voucherLiveData.setValue(vouchers);
                isVoucherLoading.setValue(false);
                checkLoadingComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                errorMessage.setValue("Cannot load vouchers: " + throwable.getMessage());
                isVoucherLoading.setValue(false);
                checkLoadingComplete();
            }
        });
    }

    public void placeOrderWithTransaction(OrderTransactionRequest request) {

        transactionRepository.placeOrderWithTransaction(request, new TransactionRepository.OrderTransactionCallback() {
            @Override
            public void onSuccess(ApiResponse<OrderTransactionResponse> response) {
                if (response.isSuccess()) {
                    orderTransactionLiveData.postValue(response);
                } else {
                    errorMessage.postValue(response.getMessage());
                }
            }

            @Override
            public void onError(Throwable throwable) {

                errorMessage.postValue("Network error: " + throwable.getMessage());
            }
        });
    }


    private void checkLoadingComplete() {
        if (!isStoreLoading.getValue() && !isProductLoading.getValue() && !isVoucherLoading.getValue()) {
            isLoading.setValue(false);
        }
    }



}
