package com.example.ftopapplication.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftopapplication.R;
import com.example.ftopapplication.adapter.StoreAdapter;
import com.example.ftopapplication.adapter.VoucherAdapter;
import com.example.ftopapplication.data.model.Store;
import com.example.ftopapplication.data.model.Voucher;
import com.example.ftopapplication.data.repository.StoreRepository;
import com.example.ftopapplication.data.repository.VoucherRepository;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;
import com.example.ftopapplication.ui.store.StoreDetailActivity;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvVouchers, rvStores;
    private VoucherAdapter voucherAdapter;
    private StoreAdapter storeAdapter;
    private VoucherRepository voucherRepository;
    private StoreRepository storeRepository;
    private ProgressBar progressBar;
    private TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupRecyclerViews();
        initRepositories();
        fetchData();
    }

    /**
     * Phương thức khởi tạo các view
     */
    private void initViews() {
        rvVouchers = findViewById(R.id.rv_voucher_list);
        rvStores = findViewById(R.id.rv_store_list);
        progressBar = findViewById(R.id.progress_bar);
        tvErrorMessage = findViewById(R.id.tv_error_message);
    }

    /**
     * Phương thức cài đặt LayoutManager và Adapter cho RecyclerViews
     */
    private void setupRecyclerViews() {
        rvVouchers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvStores.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Khởi tạo các Repository
     */
    private void initRepositories() {
        ApiService apiService = ApiClient.getApiService();
        voucherRepository = new VoucherRepository(apiService);
        storeRepository = new StoreRepository(apiService);
    }

    /**
     * Gọi API để lấy dữ liệu cho các danh sách
     */
    private void fetchData() {
        showLoading(true);
        fetchVouchers();
        fetchStores();
    }

    private void fetchVouchers() {
        voucherRepository.getVouchers(new VoucherRepository.VoucherCallback() {
            @Override
            public void onSuccess(List<Voucher> vouchers) {
                if (vouchers != null && !vouchers.isEmpty()) {
                    Log.d("HomeActivity", "Vouchers loaded successfully.");
                    if (voucherAdapter == null) {
                        voucherAdapter = new VoucherAdapter(vouchers);
                        rvVouchers.setAdapter(voucherAdapter);
                    } else {
                        voucherAdapter.updateData(vouchers);
                    }
                } else {
                    Log.d("HomeActivity", "Voucher list is empty.");
                    showErrorMessage("Danh sách voucher trống");
                }
                checkDataLoaded();
            }

            @Override
            public void onError(Throwable throwable) {
                handleApiError("Error fetching vouchers: " + throwable.getMessage());
            }
        });
    }

    private void fetchStores() {
        storeRepository.getStores(new StoreRepository.StoreListCallback() {
            @Override
            public void onSuccess(List<Store> stores) {
                if (stores != null && !stores.isEmpty()) {
                    Log.d("HomeActivity", "Stores loaded successfully.");
                    if (storeAdapter == null) {
                        storeAdapter = new StoreAdapter(stores, store -> {
                            Intent intent = new Intent(HomeActivity.this, StoreDetailActivity.class);
                            intent.putExtra("storeId", store.getStoreId());
                            startActivity(intent);
                        });
                        rvStores.setAdapter(storeAdapter);
                    } else {
                        storeAdapter.updateData(stores);
                    }
                } else {
                    Log.d("HomeActivity", "Store list is empty.");
                    showErrorMessage("Danh sách cửa hàng trống");
                }
                checkDataLoaded();
            }

            @Override
            public void onError(Throwable throwable) {
                handleApiError("Error fetching stores: " + throwable.getMessage());
            }
        });
    }

    /**
     * Ẩn loading và hiển thị thông báo lỗi
     */
    private void handleApiError(String errorMessage) {
        Log.e("HomeActivity", errorMessage);
        showErrorMessage("Không thể tải dữ liệu. Vui lòng thử lại!");
        checkDataLoaded();
    }

    /**
     * Hiển thị hoặc ẩn ProgressBar
     */
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    /**
     * Hiển thị thông báo lỗi
     */
    private void showErrorMessage(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Kiểm tra xem tất cả các dữ liệu đã được tải xong chưa
     */
    private void checkDataLoaded() {
        if (rvVouchers.getAdapter() != null && rvStores.getAdapter() != null) {
            showLoading(false);
        }
    }
}
