package com.example.ftopapplication.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;
import com.example.ftopapplication.R;

public class SendActivity extends AppCompatActivity implements NumberPadFragment.OnNumberPadClickListener {

    private TextView tvAmount;
    private Button btnContinue;
    private StringBuilder inputAmount = new StringBuilder("0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        tvAmount = findViewById(R.id.tv_amount);
        btnContinue = findViewById(R.id.btn_continue);

        // Initialize NumberPadFragment
        NumberPadFragment numberPadFragment = new NumberPadFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.number_pad_fragment, numberPadFragment);
        transaction.commit();

        // Setup Preset Amount Buttons
        findViewById(R.id.btn_100).setOnClickListener(v -> updateAmount(100f));
        findViewById(R.id.btn_500).setOnClickListener(v -> updateAmount(500f));
        findViewById(R.id.btn_1000).setOnClickListener(v -> updateAmount(1000f));
        findViewById(R.id.btn_1500).setOnClickListener(v -> updateAmount(1500f));

        // Handle Continue Button
        btnContinue.setOnClickListener(v -> {
            if (btnContinue.isEnabled()) {
                navigateToSelectContact();
            }
        });

        // Initial state
        updateAmountDisplay();
    }

    private void updateAmount(float amount) {
        inputAmount = new StringBuilder(String.valueOf((int) amount)); // Chuyển đổi float thành String không có phần thập phân
        updateAmountDisplay();
    }

    @Override
    public void onNumberClick(String number) {
        if (inputAmount.toString().equals("0")) {
            inputAmount = new StringBuilder(number);
        } else if (inputAmount.length() < 9) { // Giới hạn 9 chữ số
            inputAmount.append(number);
        }
        updateAmountDisplay();
    }

    @Override
    public void onBackspaceClick() {
        if (inputAmount.length() > 1) {
            inputAmount.deleteCharAt(inputAmount.length() - 1);
        } else {
            inputAmount = new StringBuilder("0");
        }
        updateAmountDisplay();
    }

    private void updateAmountDisplay() {
        String amountString = inputAmount.toString().replaceAll("[^0-9]", "");

        if (amountString.isEmpty()) {
            amountString = "0";
        }

        // Chuyển đổi số tiền từ String sang float
        float amount = Float.parseFloat(amountString);

        // Hiển thị số tiền với định dạng
        tvAmount.setText(String.format("%.0f đ", amount));

        // Kiểm tra số tiền hợp lệ
        boolean isAmountValid = amount > 0 && amount <= 1_000_000_000; // Giới hạn tối đa 1 tỷ
        btnContinue.setEnabled(isAmountValid);
    }

    private void navigateToSelectContact() {
        Intent intent = new Intent(this, SelectContactActivity.class);

        // Truyền số tiền dưới dạng float
        intent.putExtra("amount", Float.parseFloat(inputAmount.toString().replaceAll("[^0-9]", "")));
        startActivity(intent);
    }
}
