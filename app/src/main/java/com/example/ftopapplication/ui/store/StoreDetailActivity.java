package com.example.ftopapplication.ui.store;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ftopapplication.R;
import com.example.ftopapplication.adapter.ProductAdapter;
import com.example.ftopapplication.adapter.VoucherAdapter;
import com.example.ftopapplication.data.model.Product;
import com.example.ftopapplication.data.model.Store;
import com.example.ftopapplication.data.model.Voucher;
import com.example.ftopapplication.data.repository.ProductRepository;
import com.example.ftopapplication.data.repository.StoreRepository;
import com.example.ftopapplication.data.repository.VoucherRepository;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;

import java.util.List;

public class StoreDetailActivity extends AppCompatActivity {

    private ImageView ivHeaderImage;
    private TextView tvSummaryPrice, tvErrorMessage;
    private Button btnCheckout;
    private RecyclerView rvProductList, rvVoucherList;
    private ProductAdapter productAdapter;
    private VoucherAdapter voucherAdapter;
    private ProgressBar progressBar;
    private Store store;

    private ProductRepository productRepository;
    private VoucherRepository voucherRepository;
    private StoreRepository storeRepository;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        initViews();
        setupRecyclerViews();
        initRepositories();

        int storeId = getIntent().getIntExtra("storeId", -1);
        if (storeId != -1) {
            fetchAllData(storeId);
        } else {
            showErrorMessage("Store ID không hợp lệ");
        }
    }

    private void initViews() {
        ivHeaderImage = findViewById(R.id.iv_header_image);
        rvProductList = findViewById(R.id.rv_product_list);
        rvVoucherList = findViewById(R.id.rv_voucher_list);
        tvSummaryPrice = findViewById(R.id.tv_summary_price);
        btnCheckout = findViewById(R.id.btn_checkout);
        progressBar = findViewById(R.id.progress_bar);
        tvErrorMessage = findViewById(R.id.tv_error_message);

        btnCheckout.setOnClickListener(v -> handleCheckout());
    }


    private void setupRecyclerViews() {
        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(List.of(), (totalQuantity, totalPrice) ->
                tvSummaryPrice.setText(String.format("Tổng tiền: %.0f VND", totalPrice))
        );
        rvProductList.setAdapter(productAdapter);

        rvVoucherList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        voucherAdapter = new VoucherAdapter(List.of());
        rvVoucherList.setAdapter(voucherAdapter);
    }

    private void initRepositories() {
        ApiService apiService = ApiClient.getApiService();
        storeRepository = new StoreRepository(apiService);
        productRepository = new ProductRepository(apiService);
        voucherRepository = new VoucherRepository(apiService);
    }

    private void fetchAllData(int storeId) {
        showLoading(true);
        fetchStoreDetails(storeId);
        fetchVouchers(storeId);
        fetchProducts(storeId);
    }

    private void fetchStoreDetails(int storeId) {
        storeRepository.getStoreById(storeId, new StoreRepository.StoreDetailCallback() {
            @Override
            public void onSuccess(Store storeData) {
                store = storeData; // Gán dữ liệu vào biến `store`
                updateStoreInfo(store); // Cập nhật giao diện với thông tin cửa hàng
            }

            @Override
            public void onError(Throwable throwable) {
                handleApiError("Không thể tải thông tin cửa hàng");
            }
        });
    }

    private void fetchVouchers(int storeId) {
        voucherRepository.getVouchersByStoreId(storeId, new VoucherRepository.VoucherCallback() {
            @Override
            public void onSuccess(List<Voucher> vouchers) {
                voucherAdapter.updateData(vouchers);
            }

            @Override
            public void onError(Throwable throwable) {
                handleApiError("Không thể tải danh sách voucher");
            }
        });
    }

    private void fetchProducts(int storeId) {
        productRepository.getProductsByStoreId(storeId, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productAdapter.updateData(products);
            }

            @Override
            public void onError(Throwable throwable) {
                handleApiError("Không thể tải danh sách sản phẩm");
            }
        });
    }

    private void updateStoreInfo(Store store) {
        Glide.with(this).load(store.getStoreImage().get(0))
                .placeholder(R.drawable.placeholder_image)
                .into(ivHeaderImage);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(store.getStoreName());
    }

    private void handleCheckout() {
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
        resetCart();
    }

    private void resetCart() {
        productAdapter.clearCart();
        tvSummaryPrice.setText("Tổng tiền: 0 VND");
    }

    private void handleApiError(String message) {
        showLoading(false);
        showErrorMessage(message);
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        } else {
            Log.e("StoreDetailActivity", "ProgressBar chưa được khởi tạo");
        }
    }
    private void showErrorMessage(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }
}
