package com.example.ftopapplication.ui.pinentry;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.ApiResponse;
import com.example.ftopapplication.data.model.OrderTransactionRequest;
import com.example.ftopapplication.data.model.OrderTransactionResponse;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.TransactionRepository;
import com.example.ftopapplication.ui.home.HomeActivity;
import com.example.ftopapplication.ui.send.SendSuccessActivity;
import com.example.ftopapplication.ui.shared.fragment.PinNumberPadFragment;
import com.example.ftopapplication.viewmodel.pinentry.PinEntryViewModel;
import com.example.ftopapplication.viewmodel.pinentry.PinEntryViewModelFactory;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class PinEntryActivity extends AppCompatActivity implements PinNumberPadFragment.OnPinNumberPadClickListener {

    private static final int PIN_LENGTH = 6;

    private int enteredPin = 0; // Mã PIN được lưu dưới dạng số nguyên
    private LinearLayout pinDisplay;
    private TextView pinErrorMessage;
    private ImageView btnBack;

    private int amount;
    private int receiverUserId;
    private int userId; // Lưu ID người dùng
    private OrderTransactionRequest orderRequest;

    private PinEntryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_entry);

        initViewModel();
        initViews();

        // Retrieve data từ Intent
        Intent intent = getIntent();
        boolean isTransfer = intent.getBooleanExtra("is_transfer", false); // Nhận giá trị is_transfer
        amount = intent.getIntExtra("amount", 0);
        receiverUserId = intent.getIntExtra("receiver_user_id", -1);
        userId = intent.getIntExtra("transfer_user_id", -1); // Lấy transfer_user_id thay vì user_id
        int balance = intent.getIntExtra("balance", 0); // Nhận số dư ví

        if (isTransfer) {
            // Kiểm tra điều kiện dữ liệu chuyển tiền
            if (userId == -1 || receiverUserId == -1) {
                showMotionToast("Error", "Invalid user details. Please try again.", MotionToastStyle.ERROR);
                finish();
                return;
            }

        } else {
            // Xử lý logic đặt hàng
            orderRequest = intent.getParcelableExtra("orderRequest");

            if (orderRequest == null) {
                showMotionToast("Error", "Order data is invalid. Please try again.", MotionToastStyle.ERROR);
                finish();
                return;
            }
            userId = orderRequest.getUserId(); // Lấy userId từ orderRequest nếu là luồng đặt hàng
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        pinDisplay = findViewById(R.id.pin_display);
        pinErrorMessage = findViewById(R.id.pin_error_message);
        btnBack = findViewById(R.id.btn_back);

        PinNumberPadFragment numberPadFragment = (PinNumberPadFragment) getSupportFragmentManager()
                .findFragmentById(R.id.number_pad_pin_fragment);
        if (numberPadFragment != null) {
            numberPadFragment.setOnPinClickListener(this);
        }
    }

    private void initViewModel() {
        TransactionRepository repository = new TransactionRepository();
        PinEntryViewModelFactory factory = new PinEntryViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(PinEntryViewModel.class);
    }

    private void validatePin() {
        if (viewModel == null) {
            showMotionToast("Error", "ViewModel not initialized. Cannot process transaction.", MotionToastStyle.ERROR);
            return;
        }

        boolean isTransfer = getIntent().getBooleanExtra("is_transfer", false); // Nhận giá trị is_transfer
        Log.d("PinEntryActivity", "Validating PIN. Is Transfer: " + isTransfer);

        viewModel.verifyPin(userId, enteredPin, new TransactionRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (isTransfer) {
                    performTransfer();
                } else {
                    placeOrder();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if ("Incorrect PIN".equalsIgnoreCase(throwable.getMessage())) {
                    showMotionToast("PIN Error", "The PIN you entered is incorrect. Please try again.", MotionToastStyle.WARNING);
                    resetPin();
                } else {
                    showMotionToast("Error", "Failed to verify PIN. Please try again.", MotionToastStyle.ERROR);
                }
            }
        });
    }

    private void performTransfer() {
        int transferUserId = getIntent().getIntExtra("transfer_user_id", -1);
        int receiverUserId = getIntent().getIntExtra("receiver_user_id", -1);
        int transferAmount = getIntent().getIntExtra("amount", 0);
        int balance = getIntent().getIntExtra("balance", 0);

        // Kiểm tra các điều kiện
        if (balance < transferAmount) {
            showInsufficientBalanceDialog();
            return;
        }

        if (transferUserId == -1 || receiverUserId == -1) {
            showMotionToast("Error", "Invalid user details. Please try again.", MotionToastStyle.ERROR);
            return;
        }

        if (transferAmount <= 0) {
            showMotionToast("Error", "Invalid transfer amount. Please enter a valid amount.", MotionToastStyle.ERROR);
            return;
        }

        viewModel.transferMoney(transferUserId, receiverUserId, transferAmount, new TransactionRepository.SingleTransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                navigateToSuccessScreen(transaction.getTransactionAmount(), "Transfer completed successfully!");
            }

            @Override
            public void onError(Throwable throwable) {
                if ("Insufficient balance".equalsIgnoreCase(throwable.getMessage())) {
                    showMotionToast("Error", "Insufficient balance. Please top up!", MotionToastStyle.WARNING);
                } else {
                    showMotionToast("Error", "Transfer failed. Please try again.", MotionToastStyle.ERROR);
                }
            }
        });
    }

    private void placeOrder() {
        if (orderRequest != null) {
            viewModel.placeOrder(orderRequest, new TransactionRepository.OrderTransactionCallback() {
                @Override
                public void onSuccess(ApiResponse<OrderTransactionResponse> response) {
                    navigateToSuccessScreen(amount, "Order placed successfully!");
                }

                @Override
                public void onError(Throwable throwable) {
                    if ("Insufficient balance".equalsIgnoreCase(throwable.getMessage())) {
                        showInsufficientBalanceDialog();
                    } else {
                        showMotionToast("Error", "Failed to place order. Please try again.", MotionToastStyle.ERROR);
                    }
                }
            });
        } else {
            showMotionToast("Error", "Order request is null. Cannot process transaction.", MotionToastStyle.ERROR);
        }
    }

    private void showInsufficientBalanceDialog() {
        showLottieDialog(
                "Your wallet balance is less than the transfer amount. Please top up!",
                () -> {
                    // Điều hướng về màn hình Home
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("user_id", userId); // Truyền userId về Home
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
        );
    }



    private void resetPin() {
        enteredPin = 0;
        updatePinDisplay();
    }

    private void updatePinDisplay() {
        for (int i = 0; i < PIN_LENGTH; i++) {
            View dot = pinDisplay.getChildAt(i);
            if (dot != null) {
                dot.setBackgroundResource(i < String.valueOf(enteredPin).length() ? R.drawable.dot_filled : R.drawable.dot_empty);
            }
        }
    }

    @Override
    public void onNumberClick(String number) {
        if (String.valueOf(enteredPin).length() < PIN_LENGTH) {
            enteredPin = enteredPin * 10 + Integer.parseInt(number);
            updatePinDisplay();

            if (String.valueOf(enteredPin).length() == PIN_LENGTH) {
                validatePin();
            }
        }
    }

    @Override
    public void onBackspaceClick() {
        if (enteredPin > 0) {
            enteredPin = enteredPin / 10;
            updatePinDisplay();
        }
    }

    private void navigateToSuccessScreen(int amount, String message) {
        Intent intent = new Intent(this, SendSuccessActivity.class);
        intent.putExtra("user_id", userId);
        intent.putExtra("amount", String.format("%d đ", amount));
        intent.putExtra("message", message);
        startActivity(intent);
        finish();
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

    private void showLottieDialog(String message, Runnable onPositiveButtonClick) {
        // Inflate layout
        View dialogView = getLayoutInflater().inflate(R.layout.lottie_dialog, null);

        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Ánh xạ view trong layout
        LottieAnimationView lottieAnimationView = dialogView.findViewById(R.id.lottieAnimationView);
        TextView tvDialogMessage = dialogView.findViewById(R.id.tvDialogMessage);
        Button btnGoToHome = dialogView.findViewById(R.id.btnGoToHome);

        // Set thông điệp cho dialog
        tvDialogMessage.setText(message);

        // Gán sự kiện cho button
        btnGoToHome.setOnClickListener(v -> {
            dialog.dismiss(); // Đóng dialog
            if (onPositiveButtonClick != null) {
                onPositiveButtonClick.run(); // Thực hiện hành động
            }
        });

        // Hiển thị dialog
        dialog.show();
    }

}
