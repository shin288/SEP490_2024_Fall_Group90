package com.example.ftopapplication.ui.pinentry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.OrderTransactionRequest;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.repository.TransactionRepository;
import com.example.ftopapplication.ui.send.SendActivity;
import com.example.ftopapplication.ui.send.SendSuccessActivity;
import com.example.ftopapplication.ui.shared.fragment.PinNumberPadFragment;
import com.example.ftopapplication.viewmodel.pinentry.PinEntryViewModel;
import com.example.ftopapplication.viewmodel.pinentry.PinEntryViewModelFactory;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PinEntryActivity extends AppCompatActivity implements PinNumberPadFragment.OnPinNumberPadClickListener {

    private static final String CORRECT_PIN = "123456"; // Default PIN
    private static final int PIN_LENGTH = 6;

    private StringBuilder enteredPin = new StringBuilder();
    private LinearLayout pinDisplay;
    private TextView pinErrorMessage;
    private ImageView btnBack;

    private int amount; // Transfer amount
    private String receiverName;
    private String receiverPhone;
    private int transferUserId ; // Default sender ID
    private int receiverUserId; // Receiver ID

    private TransactionRepository transactionRepository;
    private OrderTransactionRequest orderRequest;
    private PinEntryViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_entry);

        initViewModel();

        // Initialize views
        pinDisplay = findViewById(R.id.pin_display);
        pinErrorMessage = findViewById(R.id.pin_error_message);
        btnBack = findViewById(R.id.btn_back);

        transactionRepository = new TransactionRepository();

        // Retrieve data from Intent
        Intent intent = getIntent();
        amount = intent.getIntExtra("amount", 0);
        receiverName = intent.getStringExtra("receiver_name");
        receiverPhone = intent.getStringExtra("receiver_phone");
        receiverUserId = intent.getIntExtra("receiver_id", -1);

        orderRequest = intent.getParcelableExtra("orderRequest");

        if (orderRequest == null) {
            Log.e("PinEntryActivity", "Intent is null. Cannot proceed.");
            Toast.makeText(this, "Invalid order data. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("PinEntryActivity", "OrderTransactionRequest: " + orderRequest.toString());
        // Nếu giá trị receiverUserId vẫn là -1
        if (receiverUserId == -1) {
            Toast.makeText(this, "Invalid receiver . Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Back button event
        btnBack.setOnClickListener(v -> finish());

        // Integrate number pad fragment
        PinNumberPadFragment numberPadFragment = (PinNumberPadFragment) getSupportFragmentManager()
                .findFragmentById(R.id.number_pad_pin_fragment);
        if (numberPadFragment != null) {
            numberPadFragment.setOnPinClickListener(this); // Attach listener
        }
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
        TransactionRepository repository = new TransactionRepository(); // Khởi tạo repository
        PinEntryViewModelFactory factory = new PinEntryViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(PinEntryViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, this::showLoading);

        // Xử lý phản hồi từ placeOrderWithTransaction
        viewModel.getOrderResponseLiveData().observe(this, response -> {
            if (response != null && response.getData() != null) {
                Log.d("StoreDetailActivity", "Order placed successfully: " + response.getData());
                navigateToSuccessScreen(response.getData().getOrder().getTotalPrice(), "Order placed successfully!");
            } else {
                showErrorMessage("Failed to place order. Please try again.");
            }
        });

        // Xử lý phản hồi từ transferMoney
        viewModel.getTransferResponseLiveData().observe(this, transaction -> {
            if (transaction != null) {
                // Chuyển sang màn hình thành công khi chuyển tiền thành công
                navigateToSuccessScreen(transaction.getTransactionAmount(), "Money transferred successfully!");
            } else {
                showErrorMessage("Failed to transfer money. Please try again.");
            }
        });

        viewModel.getErrorMessage().observe(this, this::showErrorMessage);
    }

    private void showErrorMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void showLoading(Boolean aBoolean) {
    }

    @Override
    public void onNumberClick(String number) {
        addPinDigit(number);
    }

    @Override
    public void onBackspaceClick() {
        removeLastPinDigit();
    }

    private void addPinDigit(String digit) {
        if (enteredPin.length() < PIN_LENGTH) {
            enteredPin.append(digit);
            updatePinDisplay();

            if (enteredPin.length() == PIN_LENGTH) {
                validatePin();
            }
        }
    }

    private void removeLastPinDigit() {
        if (enteredPin.length() > 0) {
            enteredPin.deleteCharAt(enteredPin.length() - 1);
            updatePinDisplay();
        }
    }

    private void updatePinDisplay() {
        for (int i = 0; i < PIN_LENGTH; i++) {
            View dot = pinDisplay.getChildAt(i);
            if (dot != null) {
                dot.setBackgroundResource(i < enteredPin.length() ? R.drawable.dot_filled : R.drawable.dot_empty);
            }
        }
    }

    private void validatePin() {
        if (viewModel == null) {
            showErrorMessage("ViewModel not initialized. Cannot process order.");
            return;
        }
        if (enteredPin.toString().equals(CORRECT_PIN)) {
            if (orderRequest != null) {
                viewModel.placeOrder(orderRequest); // Gọi API placeOrderWithTransaction

                // Quan sát kết quả từ ViewModel
                viewModel.getOrderResponseLiveData().observe(this, response -> {
                    if (response != null && response.getMessage() != null && response.getMessage().equalsIgnoreCase("Order and transaction processed successfully")) {
                        Log.d("PinEntryActivity", "Order placed successfully: " + response);

                        // Chuyển sang màn hình thành công
                        navigateToSuccessScreen(amount, "Order placed successfully!");
                    } else {
                        Log.e("PinEntryActivity", "Failed to place order. Response: " + response);
                        showErrorMessage("Failed to place order. Please try again.");
                    }
                });

            } else {
                showErrorMessage("Order request is null. Cannot process transaction.");
            }
        } else {
            showPinError();
        }
    }



    private void showPinError() {
        pinErrorMessage.setVisibility(View.VISIBLE);
        pinErrorMessage.setText("The PIN you entered is incorrect. Please try again.");
        enteredPin.setLength(0); // Clear entered PIN
        updatePinDisplay();
    }

    private void initiateTransfer() {
        Transaction transaction = new Transaction();
        transaction.setTransferUserId(transferUserId);
        transaction.setReceiveUserId(receiverUserId);
        transaction.setTransactionAmount(amount);
        transaction.setTransactionDescription("Money Transfer");

        viewModel.transferMoney(transaction); // Call transferMoney API
    }


    private void showTransferFailedDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Transfer Failed")
                .setMessage(message)
                .setPositiveButton("Retry", (dialog, which) -> {
                    Intent intent = new Intent(this, SendActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void navigateToSuccessScreen(int amount, String message) {
        Intent intent = new Intent(this, SendSuccessActivity.class);
        intent.putExtra("amount", String.format("%d đ", amount));
        intent.putExtra("name", receiverName);

        intent.putExtra("time", "3:02 PM"); // Replace with real-time data
        intent.putExtra("user_id", orderRequest.getUserId());
        Log.d("pinentry", "user_id: " + orderRequest.getUserId());
        startActivity(intent);
        finish();
    }
}
