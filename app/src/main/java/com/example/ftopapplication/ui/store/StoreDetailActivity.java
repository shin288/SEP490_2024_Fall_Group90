package com.example.ftopapplication.ui.store;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ftopapplication.R;
import com.example.ftopapplication.adapter.ProductAdapter;
import com.example.ftopapplication.adapter.VoucherAdapter;
import com.example.ftopapplication.data.model.OrderTransactionRequest;
import com.example.ftopapplication.data.model.Product;
import com.example.ftopapplication.data.model.ProductOrder;
import com.example.ftopapplication.data.model.Store;
import com.example.ftopapplication.ui.pinentry.PinEntryActivity;
import com.example.ftopapplication.viewmodel.store.StoreDetailViewModel;
import com.example.ftopapplication.viewmodel.store.StoreDetailViewModelFactory;
import com.example.ftopapplication.data.repository.ProductRepository;
import com.example.ftopapplication.data.repository.StoreRepository;
import com.example.ftopapplication.data.repository.VoucherRepository;
import com.example.ftopapplication.network.ApiClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StoreDetailActivity extends AppCompatActivity {

    private ImageView ivHeaderImage;
    private TextView tvSummaryPrice, tvErrorMessage;
    private Button btnCheckout;
    private RecyclerView rvProductList, rvVoucherList;
    private ProductAdapter productAdapter;
    private VoucherAdapter voucherAdapter;

    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private int previousOffset ;


    private StoreDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        initViews();
        setupRecyclerViews();
        initViewModel();

        int storeId = getIntent().getIntExtra("storeId", -1);
        if (storeId != -1) {
            viewModel.fetchAllData(storeId);
        } else {
            showErrorMessage("Store ID not available");
        }

        previousOffset = -1; // Giá trị mặc định ban đầu

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            if (verticalOffset != previousOffset) {
                previousOffset = verticalOffset;

                int totalScrollRange = appBarLayout1.getTotalScrollRange();
                if (Math.abs(verticalOffset) == totalScrollRange) {
                    // Thu nhỏ hoàn toàn
                    ivHeaderImage.setVisibility(View.GONE);
                    toolbar.setTitle("Store Detail");
                } else if (verticalOffset == 0) {
                    // Mở hoàn toàn
                    ivHeaderImage.setVisibility(View.VISIBLE);
                    toolbar.setTitle("");
                }
            }
        });

        observeViewModel();
    }

    private void initViews() {
        ivHeaderImage = findViewById(R.id.iv_header_image);
        rvProductList = findViewById(R.id.rv_product_list);
        rvVoucherList = findViewById(R.id.rv_voucher_list);
        tvSummaryPrice = findViewById(R.id.tv_summary_price);
        btnCheckout = findViewById(R.id.btn_checkout);

        tvErrorMessage = findViewById(R.id.tv_error_message);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar  = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);

        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    private void setupRecyclerViews() {
        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(List.of(), (totalQuantity, totalPrice) ->
                tvSummaryPrice.setText(String.format(Locale.getDefault(), "Total amount: %d đ", (int) totalPrice))
        );
        rvProductList.setAdapter(productAdapter);

        rvVoucherList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        voucherAdapter = new VoucherAdapter(List.of());
        rvVoucherList.setAdapter(voucherAdapter);
    }

    private void initViewModel() {
        StoreRepository storeRepository = new StoreRepository(ApiClient.getApiService());
        ProductRepository productRepository = new ProductRepository(ApiClient.getApiService());
        VoucherRepository voucherRepository = new VoucherRepository(ApiClient.getApiService());

        StoreDetailViewModelFactory factory = new StoreDetailViewModelFactory(storeRepository, productRepository, voucherRepository);
        viewModel = new ViewModelProvider(this, factory).get(StoreDetailViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getStoreLiveData().observe(this, store -> {
            if (store != null) {
                collapsingToolbar.post(() -> {
                    collapsingToolbar.setTitle(store.getStoreName());
                    Glide.with(this).load(store.getStoreImage().get(0))
                            .placeholder(R.drawable.placeholder_image)
                            .into(ivHeaderImage);
                });
            }
        });


        viewModel.getProductLiveData().observe(this, products -> {
            if (products != null) {
                productAdapter.updateData(products);
            }
        });

        viewModel.getVoucherLiveData().observe(this, vouchers -> {
            if (vouchers != null) {
                voucherAdapter.updateData(vouchers);
            }
        });

        viewModel.getErrorMessage().observe(this, this::showErrorMessage);

        viewModel.getIsLoading().observe(this, this::showLoading);
    }

    private void updateStoreInfo(Store store) {
        Glide.with(this).load(store.getStoreImage().get(0))
                .placeholder(R.drawable.placeholder_image)
                .into(ivHeaderImage);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(store.getStoreName());
    }


    private void handleCheckout() {
        int userId = getIntent().getIntExtra("user_id", -1); // Receive userId from Intent
        int storeId = getIntent().getIntExtra("storeId", -1); // Receive storeId from Intent
        int totalPrice = productAdapter.getTotalPrice(); // Total price from adapter
        List<ProductOrder> selectedProducts = productAdapter.getSelectedProducts(); // List of selected products

        // Validate data
        if (userId == -1) {
            showErrorMessage("User information is missing. Please log in again.");
            return;
        }

        if (storeId == -1) {
            showErrorMessage("Store information is missing. Please try again.");
            return;
        }

        if (totalPrice <= 0 || selectedProducts.isEmpty()) {
            showErrorMessage("Cart data is invalid. Please check your order.");
            return;
        }

        OrderTransactionRequest request = new OrderTransactionRequest(
                userId,
                storeId,
                null, // Voucher ID (có thể null nếu không có voucher)
                selectedProducts,
                "Optional order note", // Ghi chú đơn hàng
                totalPrice
        );

        // Truyền OrderTransactionRequest qua Intent
        Intent intent = new Intent(StoreDetailActivity.this, PinEntryActivity.class);
        intent.putExtra("order_request", request); // Sử dụng Parcelable để truyền
        startActivity(intent);
    }


    private void showErrorMessage(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean isLoading) {

    }
}
