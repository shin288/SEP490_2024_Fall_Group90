package com.example.ftopapplication.viewmodel.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.Store;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.model.Voucher;
import com.example.ftopapplication.data.repository.StoreRepository;
import com.example.ftopapplication.data.repository.UserRepository;
import com.example.ftopapplication.data.repository.VoucherRepository;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final StoreRepository storeRepository;

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Voucher>> vouchersLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Store>> storesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingState = new MutableLiveData<>();
    private final MutableLiveData<String> errorState = new MutableLiveData<>();

    public HomeViewModel() {
        ApiService apiService = ApiClient.getApiService();

        userRepository = new UserRepository();
        voucherRepository = new VoucherRepository(apiService);
        storeRepository = new StoreRepository(apiService);

        loadingState.setValue(false); // Mặc định không tải dữ liệu
        errorState.setValue(null); // Không có lỗi ban đầu
    }

    /**
     * Lấy thông tin người dùng từ API
     */
    public void fetchUserInfo(int userId) {
        loadingState.setValue(true);
        errorState.setValue(null);

        userRepository.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                loadingState.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    userLiveData.postValue(response.body());
                } else {
                    errorState.postValue("Failed to fetch user data. Please try again.");
                    userLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                loadingState.setValue(false);
                errorState.postValue("Network error: " + t.getMessage());
                userLiveData.postValue(null);
            }
        });
    }

    /**
     * Lấy danh sách vouchers từ API
     */
    public void fetchVouchers() {
        loadingState.setValue(true);
        voucherRepository.getVouchers(new VoucherRepository.VoucherCallback() {
            @Override
            public void onSuccess(List<Voucher> vouchers) {
                loadingState.setValue(false);
                vouchersLiveData.postValue(vouchers);
            }

            @Override
            public void onError(Throwable throwable) {
                loadingState.setValue(false);
                errorState.postValue("Failed to load vouchers: " + throwable.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách stores từ API
     */
    public void fetchStores() {
        loadingState.setValue(true);
        storeRepository.getStores(new StoreRepository.StoreListCallback() {
            @Override
            public void onSuccess(List<Store> stores) {
                loadingState.setValue(false);
                storesLiveData.postValue(stores);
            }

            @Override
            public void onError(Throwable throwable) {
                loadingState.setValue(false);
                errorState.postValue("Failed to load stores: " + throwable.getMessage());
            }
        });
    }

    /**
     * LiveData getters
     */
    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<List<Voucher>> getVouchersLiveData() {
        return vouchersLiveData;
    }

    public LiveData<List<Store>> getStoresLiveData() {
        return storesLiveData;
    }

    public LiveData<Boolean> getLoadingState() {
        return loadingState;
    }

    public LiveData<String> getErrorState() {
        return errorState;
    }

    /**
     * Reset trạng thái lỗi
     */
    public void resetErrorState() {
        errorState.setValue(null);
    }
}
