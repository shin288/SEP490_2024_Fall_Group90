package com.example.ftopapplication.ui.pinentry;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.repository.TransactionRepository;
import com.example.ftopapplication.ui.send.SendActivity;
import com.example.ftopapplication.ui.send.SendSuccessActivity;
import com.example.ftopapplication.ui.shared.fragment.PinNumberPadFragment;

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

    private float amount; // Transfer amount
    private String receiverName;
    private String receiverPhone;
    private int transferUserId = 1; // Default sender ID
    private int receiverUserId; // Receiver ID

    private TransactionRepository transactionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_entry);

        // Initialize views
        pinDisplay = findViewById(R.id.pin_display);
        pinErrorMessage = findViewById(R.id.pin_error_message);
        btnBack = findViewById(R.id.btn_back);

        transactionRepository = new TransactionRepository();

        // Retrieve data from Intent
        Intent intent = getIntent();
        amount = intent.getFloatExtra("amount", 0.0f);
        receiverName = intent.getStringExtra("receiver_name");
        receiverPhone = intent.getStringExtra("receiver_phone");
        receiverUserId = intent.getIntExtra("receiver_user_id", -1);

        Log.d("PinEntryActivity", "Amount: " + amount);
        Log.d("PinEntryActivity", "Receiver Name: " + receiverName);
        Log.d("PinEntryActivity", "Receiver Phone: " + receiverPhone);
        Log.d("PinEntryActivity", "Receiver User ID: " + receiverUserId);

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
        if (enteredPin.toString().equals(CORRECT_PIN)) {
            initiateTransfer();
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
        // Tạo đối tượng transaction
        Transaction transaction = new Transaction();
        transaction.setTransferUserId(transferUserId);
        transaction.setReceiveUserId(receiverUserId);
        transaction.setTransactionAmount(amount);
        transaction.setTransactionDescription("Money Transfer");

        Log.d("PinEntryActivity", "Transaction Data: " + transaction.toString());

        // Gọi API transferMoney
        transactionRepository.transferMoney(transaction).enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.isSuccessful()) {
                    navigateToSuccessScreen();
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("PinEntryActivity", "API Error Response: " + errorBody);
                    } catch (Exception e) {
                        Log.e("PinEntryActivity", "Error parsing error response", e);
                    }
                    showTransferFailedDialog("Transaction failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Log.e("PinEntryActivity", "API Failure: " + t.getMessage());
                showTransferFailedDialog("Network error: " + t.getMessage());
            }
        });
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

    private void navigateToSuccessScreen() {
        Intent intent = new Intent(this, SendSuccessActivity.class);
        intent.putExtra("amount", String.format("%.2f đ", amount));
        intent.putExtra("name", receiverName);
        intent.putExtra("phone", receiverPhone);
        intent.putExtra("time", "3:02 PM"); // Replace with real-time data
        startActivity(intent);
        finish();
    }
}
