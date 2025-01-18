package com.example.ftopapplication.ui.store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.ftopapplication.R;
import com.example.ftopapplication.adapter.ProductAdapter;
import com.example.ftopapplication.adapter.VoucherAdapter;
import com.example.ftopapplication.data.model.OrderTransactionRequest;
import com.example.ftopapplication.data.model.Product;
import com.example.ftopapplication.data.model.ProductOrder;
import com.example.ftopapplication.data.model.Store;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.model.Voucher;
import com.example.ftopapplication.data.repository.TransactionRepository;
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
    private TextView tvSummaryPrice, tvErrorMessage, tvOriginalTotal, tvVoucherDiscount;
    private Button btnCheckout;
    private RecyclerView rvProductList, rvVoucherList;
    private ProductAdapter productAdapter;
    private VoucherAdapter voucherAdapter;

    private LottieAnimationView lottieViewDetail;
    private NestedScrollView detailScrollView;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private int previousOffset ;
    private static final String BASE_URL = "http://172.20.80.1:8000";


    private StoreDetailViewModel viewModel;
    private boolean isDetailVisible = false;

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

        lottieViewDetail.setOnClickListener(v -> toggleDetailDisplay());
    }

    private void initViews() {
        ivHeaderImage = findViewById(R.id.iv_header_image);
        rvProductList = findViewById(R.id.rv_product_list);
        rvVoucherList = findViewById(R.id.rv_voucher_list);
        tvSummaryPrice = findViewById(R.id.tv_summary_price);
        lottieViewDetail = findViewById(R.id.lottie_view_detail);
        btnCheckout = findViewById(R.id.btn_checkout);
        detailScrollView = findViewById(R.id.detailScrollView);

        tvOriginalTotal = findViewById(R.id.tv_original_total);
        tvVoucherDiscount = findViewById(R.id.tv_voucher_discount);
        if (btnCheckout == null) {

        }

        tvErrorMessage = findViewById(R.id.tv_error_message);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar  = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);

        btnCheckout.setOnClickListener(v -> handleCheckout());

        tvOriginalTotal.setVisibility(View.GONE);
        tvVoucherDiscount.setVisibility(View.GONE);
    }

    private void setupRecyclerViews() {
        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(List.of(), (totalQuantity, totalPrice) -> {
            updateBottomSheet();  // Cập nhật thông tin giá tiền khi thay đổi số lượng sản phẩm

            // Kiểm tra nếu không chọn sản phẩm nào thì disable nút Checkout
            if (totalQuantity > 0) {
                btnCheckout.setEnabled(true);
                btnCheckout.setBackgroundTintList(getResources().getColorStateList(R.color.blue_border));
            } else {
                btnCheckout.setEnabled(false);
                btnCheckout.setBackgroundTintList(getResources().getColorStateList(R.color.gray));  // Màu xám khi disable
            }
        });
        rvProductList.setAdapter(productAdapter);

        rvVoucherList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        voucherAdapter = new VoucherAdapter(List.of(), voucher -> {
            if (voucher != null) {
                // Nếu voucher được chọn
                int discount = voucher.getVoucherDiscount();
                productAdapter.setSelectedVoucherDiscount(discount);

                // Tính lại tổng tiền
                int updatedTotalPrice = productAdapter.getTotalPrice();
                tvSummaryPrice.setText(String.format(Locale.getDefault(), "Total amount: %d đ", updatedTotalPrice));
            } else {
                // Nếu voucher bị hủy chọn
                productAdapter.setSelectedVoucherDiscount(0); // Không có giảm giá
                int updatedTotalPrice = productAdapter.getTotalPrice();
                tvSummaryPrice.setText(String.format(Locale.getDefault(), "Total amount: %d đ", updatedTotalPrice));
            }
            updateBottomSheet();
        });
        rvVoucherList.setAdapter(voucherAdapter);

        // Mặc định disable nút Checkout khi chưa chọn sản phẩm
        btnCheckout.setEnabled(false);
        btnCheckout.setBackgroundTintList(getResources().getColorStateList(R.color.gray));

    }
    private void updateBottomSheet() {
        int originalTotalPrice = productAdapter.getOriginalTotalPrice();
        Voucher selectedVoucher = voucherAdapter.getSelectedVoucher();

        int discount = (selectedVoucher != null) ? (originalTotalPrice * selectedVoucher.getVoucherDiscount()) / 100 : 0;
        int finalTotalPrice = originalTotalPrice - discount;

        // Cập nhật thông tin trên Bottom Sheet
        tvOriginalTotal.setText(String.format(Locale.getDefault(), "Original Total: %,d đ", originalTotalPrice));
        tvVoucherDiscount.setText(String.format(Locale.getDefault(), "Voucher Discount: -%,d đ", discount));
        tvSummaryPrice.setText(String.format(Locale.getDefault(), "Total amount: %,d đ", finalTotalPrice));
    }


    private void toggleDetailDisplay() {
        if (isDetailVisible) {
            tvOriginalTotal.setVisibility(View.GONE);
            tvVoucherDiscount.setVisibility(View.GONE);
            isDetailVisible = false;
        } else {
            showDetailAndScrollUp();
            isDetailVisible = true;
        }
    }

    // Hiển thị thông tin và scroll lên
    private void showDetailAndScrollUp() {
        int originalTotalPrice = productAdapter.getOriginalTotalPrice();
        int discountPercent = 0;
        int discountAmount = 0;

        Voucher selectedVoucher = voucherAdapter.getSelectedVoucher();
        if (selectedVoucher != null) {
            discountPercent = selectedVoucher.getVoucherDiscount();
            discountAmount = originalTotalPrice * discountPercent / 100;
        }

        tvOriginalTotal.setText(String.format(Locale.getDefault(), "Original Total: %,d đ", originalTotalPrice));
        tvVoucherDiscount.setText(String.format(Locale.getDefault(), "Voucher Discount: -%,d đ", discountAmount));

        tvOriginalTotal.setVisibility(View.VISIBLE);
        tvVoucherDiscount.setVisibility(View.VISIBLE);

        detailScrollView.post(() -> detailScrollView.fullScroll(View.FOCUS_UP));
    }


    private void initViewModel() {
        StoreRepository storeRepository = new StoreRepository(ApiClient.getApiService());
        ProductRepository productRepository = new ProductRepository(ApiClient.getApiService());
        VoucherRepository voucherRepository = new VoucherRepository(ApiClient.getApiService());
        TransactionRepository transactionRepository = new TransactionRepository(ApiClient.getApiService());

        StoreDetailViewModelFactory factory = new StoreDetailViewModelFactory(storeRepository, productRepository, voucherRepository,transactionRepository);
        viewModel = new ViewModelProvider(this, factory).get(StoreDetailViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getStoreLiveData().observe(this, store -> {
            if (store != null && store.getStoreImage() != null && !store.getStoreImage().isEmpty()) {
                // Load ảnh giống StoreAdapter
                loadImage(this, ivHeaderImage, BASE_URL, store.getStoreImage().get(0));

                // Cập nhật tiêu đề
                collapsingToolbar.setTitle(store.getStoreName());
            } else {
                ivHeaderImage.setImageResource(R.drawable.placeholder_image);
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

    private void loadImage(Context context, ImageView imageView, String baseUrl, String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(context)
                    .load(baseUrl + imagePath)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)  // Hiển thị ảnh lỗi nếu load không thành công
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void updateStoreInfo(Store store) {
        Glide.with(this).load(store.getStoreImage().get(0))
                .placeholder(R.drawable.placeholder_image)
                .into(ivHeaderImage);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(store.getStoreName());
    }


    private void handleCheckout() {
        // Lấy userId và storeId từ Intent
        int userId = getIntent().getIntExtra("user_id", -1);
        int storeId = getIntent().getIntExtra("storeId", -1);

        // Lắng nghe dữ liệu từ ViewModel
        viewModel.getStoreLiveData().observe(this, store -> {
            if (store != null) {
                // Hiển thị thông tin store
                collapsingToolbar.post(() -> {
                    collapsingToolbar.setTitle(store.getStoreName());
                    Glide.with(this)
                            .load(store.getStoreImage().get(0))
                            .placeholder(R.drawable.placeholder_image)
                            .into(ivHeaderImage);
                });

                // Lấy userId của store (chủ sở hữu)
                User storeUser = store.getUser(); // Assumes Store model has a User object
                if (storeUser != null) {
                    int receiverId = storeUser.getId(); // ID của owner

                    // Lấy thông tin từ Adapter
                    int totalPrice = productAdapter.getTotalPrice();
                    List<ProductOrder> selectedProducts = productAdapter.getSelectedProducts();

                    // Kiểm tra dữ liệu
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

                    // Tạo request để truyền đi
                    OrderTransactionRequest request = new OrderTransactionRequest(
                            userId,
                            storeId,
                            null, // Voucher ID (nullable nếu không áp dụng voucher)
                            selectedProducts,
                            "Optional order note", // Ghi chú đơn hàng (tùy chọn)
                            totalPrice
                    );

                    // Truyền dữ liệu qua PinEntryActivity
                    Intent intent = new Intent(StoreDetailActivity.this, PinEntryActivity.class);
                    intent.putExtra("orderRequest", request); // Truyền Parcelable object

                    intent.putExtra("amount", totalPrice); // Tổng giá tiền
                    intent.putExtra("receiver_id", receiverId); // ID của người nhận
                    intent.putExtra("store_name", collapsingToolbar.getTitle().toString()); // Tên cửa hàng
                    Log.d("StoreDetailActivity", "Intent Content: " + intent.getExtras());
                    startActivity(intent);
                } else {
                    showErrorMessage("Store owner information is missing.");
                }
            } else {
                showErrorMessage("Store data is unavailable.");
            }
        });
    }

    private void showErrorMessage(String message) {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

    }

    private void showLoading(boolean isLoading) {

    }
}
