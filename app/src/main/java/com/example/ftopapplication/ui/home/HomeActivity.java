package com.example.ftopapplication.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftopapplication.R;
import com.example.ftopapplication.adapter.StoreAdapter;
import com.example.ftopapplication.adapter.VoucherAdapter;
import com.example.ftopapplication.ui.send.SendActivity;
import com.example.ftopapplication.ui.store.StoreDetailActivity;
import com.example.ftopapplication.ui.topup.TopUpActivity;
import com.example.ftopapplication.ui.transaction.StaticActivity;
import com.example.ftopapplication.viewmodel.home.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvVouchers, rvStores;
    private VoucherAdapter voucherAdapter;
    private StoreAdapter storeAdapter;
    private ProgressBar progressBar;
    private TextView tvDisplayName, tvBalanceValue, tvErrorMessage, tvNoVoucher, tvNoStore;
    private ImageView ivEyeIcon;
    private HomeViewModel viewModel;
    private boolean isBalanceVisible = true; // Trạng thái ẩn/hiện số dư

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupRecyclerViews();

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Quan sát trạng thái loading
        viewModel.getLoadingState().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                tvErrorMessage.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        // Quan sát lỗi
        viewModel.getErrorState().observe(this, errorMessage -> {
            if (errorMessage != null) {
                showErrorMessage(errorMessage);
                viewModel.resetErrorState();
            }
        });
        // Quan sát danh sách voucher
        viewModel.getVouchersLiveData().observe(this, vouchers -> {
            boolean hasData = vouchers != null && !vouchers.isEmpty();
            rvVouchers.setVisibility(hasData ? View.VISIBLE : View.GONE);
            findViewById(R.id.tv_no_voucher).setVisibility(hasData ? View.GONE : View.VISIBLE);

            if (hasData) {
                if (voucherAdapter == null) {
                    voucherAdapter = new VoucherAdapter(vouchers);
                    rvVouchers.setAdapter(voucherAdapter);
                } else {
                    voucherAdapter.updateData(vouchers);
                }
            }
        });

        // Quan sát danh sách store
        viewModel.getStoresLiveData().observe(this, stores -> {
            boolean hasData = stores != null && !stores.isEmpty();
            rvStores.setVisibility(hasData ? View.VISIBLE : View.GONE);
            findViewById(R.id.tv_no_store).setVisibility(hasData ? View.GONE : View.VISIBLE);

            if (hasData) {
                if (storeAdapter == null) {
                    storeAdapter = new StoreAdapter(stores, store -> {
                        Intent intent = new Intent(HomeActivity.this, StoreDetailActivity.class);
                        intent.putExtra("storeId", store.getStoreId());
                        intent.putExtra("user_id", getIntent().getIntExtra("user_id", -1)); // Truyền thêm userId
                        startActivity(intent);
                    });
                    rvStores.setAdapter(storeAdapter);
                } else {
                    storeAdapter.updateData(stores);
                }
            }
        });

        // Gọi API lấy dữ liệu
        viewModel.fetchVouchers();
        viewModel.fetchStores();

        int userId = getIntent().getIntExtra("user_id", -1);
        if (userId != -1) {
            viewModel.fetchUserInfo(userId);
        } else {
            showMotionToast("Error", "User ID not found!", MotionToastStyle.ERROR);
        }

        setupBottomNavigation(userId);
        viewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                tvDisplayName.setText(user.getDisplayName());
                tvBalanceValue.setText(String.format("%d đ", user.getWalletBalance()));
            }
        });

    }

    private void initViews() {

        rvVouchers = findViewById(R.id.rv_voucher_list);
        rvStores = findViewById(R.id.rv_store_list);
        progressBar = findViewById(R.id.progress_bar);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        tvDisplayName = findViewById(R.id.tv_display_name);
        tvBalanceValue = findViewById(R.id.tv_balance_value);
        tvNoVoucher = findViewById(R.id.tv_no_voucher);
        tvNoStore = findViewById(R.id.tv_no_store);
        ivEyeIcon = findViewById(R.id.iv_eye_icon);

        ImageView btnTopUp = findViewById(R.id.btn_top_up);
        if (btnTopUp != null) {
            btnTopUp.setOnClickListener(v -> {


                Intent intent = new Intent(HomeActivity.this, TopUpActivity.class);
                int userId = getIntent().getIntExtra("user_id", -1);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            });
        } else {
            Log.e("HomeActivity", "btn_top_up not exist in layout");
        }

        ImageView btnSend = findViewById(R.id.btn_send);
        if (btnSend != null) {
            btnSend.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, SendActivity.class);
                int userId = getIntent().getIntExtra("user_id", -1);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            });
        } else {
            Log.e("HomeActivity", "btn_send not exist in layout");
        }

        ImageView btnRequest = findViewById(R.id.btn_request);
        if (btnRequest != null) {
            btnSend.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, SendActivity.class);
                int userId = getIntent().getIntExtra("user_id", -1);
                intent.putExtra("user_id", userId);
                startActivity(intent);
            });
        } else {
            Log.e("HomeActivity", "btn_request not exist in layout");
        }

        // Xử lý ẩn/hiện số dư khi nhấn vào icon mắt
        ivEyeIcon.setOnClickListener(v -> toggleBalanceVisibility());
    }
    //Cấu hình và xử lý sự kiện của Bottom Navigation

    private void setupBottomNavigation(int userId) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                return true;
            } else if (itemId == R.id.menu_cashflow) {
                Intent intent = new Intent(HomeActivity.this, StaticActivity.class);
                intent.putExtra("user_id", userId); // Truyền userId
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
    }

    private void setupRecyclerViews() {
        rvVouchers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvStores.setLayoutManager(new LinearLayoutManager(this));
    }

     // Xử lý ẩn/hiện số dư
    private void toggleBalanceVisibility() {
        if (isBalanceVisible) {
            tvBalanceValue.setText("••••••");
            ivEyeIcon.setImageResource(R.drawable.ic_eye_closed); // Icon mắt đóng
        } else {
            int userId = getSharedPreferences("UserSession", MODE_PRIVATE).getInt("user_id", -1);
            if (userId != -1) {
                viewModel.getUserLiveData().observe(this, user -> {
                    if (user != null) {
                        tvBalanceValue.setText(String.format("%d đ", user.getWalletBalance()));
                    }
                });
            }
            ivEyeIcon.setImageResource(R.drawable.ic_eye); // Icon mắt mở
        }
        isBalanceVisible = !isBalanceVisible; // Đổi trạng thái
    }
    /**
     * Hiển thị thông báo lỗi
     */
    private void showErrorMessage(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void showMotionToast(String title, String message, MotionToastStyle style) {
        MotionToast.Companion.darkColorToast(this,
                title,
                message,
                style,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
    }
}
