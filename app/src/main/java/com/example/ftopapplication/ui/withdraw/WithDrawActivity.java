package com.example.ftopapplication.ui.withdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.core.content.res.ResourcesCompat;

import com.example.ftopapplication.R;
import com.example.ftopapplication.viewmodel.withdraw.WithDrawViewModel;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class WithDrawActivity extends AppCompatActivity {

    private Spinner spinnerBankName;
    private EditText etAccountNumber, etAmount;
    private Button btnWithdraw;
    private ImageView btnBack;
    private WithDrawViewModel withDrawViewModel;
    private int currentBalance; // Số dư tài khoản

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_with_draw);

        // Adjust window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        spinnerBankName = findViewById(R.id.spinner_bank_name);
        etAccountNumber = findViewById(R.id.et_account_number);
        etAmount = findViewById(R.id.et_amount);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        // Nhận số dư từ Intent
        currentBalance = getIntent().getIntExtra("balance", 0);

        // Initialize ViewModel
        withDrawViewModel = new ViewModelProvider(this).get(WithDrawViewModel.class);

        // Setup Spinner với danh sách ngân hàng
        String[] bankNames = {"Vietcombank", "Techcombank", "Viettinbank", "TPBank", "BIDV", "MBBank"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bankNames);
        spinnerBankName.setAdapter(adapter);

        // Observe success status
        withDrawViewModel.getIsSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                showMotionToast("Success", "Withdraw successful!", MotionToastStyle.SUCCESS);
                finish();
            } else {
                showMotionToast("Error", "Error, failed to withdraw", MotionToastStyle.ERROR);
            }
        });

        // Xử lý sự kiện khi nhấn nút Withdraw
        btnWithdraw.setOnClickListener(v -> {
            String bankName = spinnerBankName.getSelectedItem().toString();
            String accountNumberStr = etAccountNumber.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            // Kiểm tra các trường nhập liệu
            if (accountNumberStr.isEmpty() || amountStr.isEmpty()) {
                showMotionToast("Warning", "Please fill in all fields", MotionToastStyle.WARNING);
                return;
            }

            if (accountNumberStr.length() > 8) {
                showMotionToast("Error", "Account number must be at most 8 digits.", MotionToastStyle.ERROR);
                return;
            }

            int accountNumber;
            int amount;
            try {
                accountNumber = Integer.parseInt(accountNumberStr);
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                showMotionToast("Error", "Invalid account number or amount", MotionToastStyle.ERROR);
                return;
            }



            // Kiểm tra số dư tài khoản
            if (amount > currentBalance) {
                showMotionToast("Error", "Insufficient balance, please enter a valid withdrawal amount.", MotionToastStyle.ERROR);
                return;
            }

            int userId = getIntent().getIntExtra("user_id", -1);
            if (userId == -1) {
                showMotionToast("Error", "Invalid user ID", MotionToastStyle.ERROR);
                return;
            }

            // Gán dữ liệu vào ViewModel
            withDrawViewModel.setBankName(bankName);
            withDrawViewModel.setAccountNumber(accountNumber);
            withDrawViewModel.setAmount(amount);

            // Thực hiện giao dịch rút tiền
            withDrawViewModel.performWithdraw(userId);

            // Chuyển sang màn hình WithdrawSuccess
            Intent intent = new Intent(WithDrawActivity.this, WithDrawSuccess.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("bank_name", bankName);
            intent.putExtra("account_number", accountNumber); // Dữ liệu là int
            intent.putExtra("amount", amount);               // Dữ liệu là int
            startActivity(intent);
            finish();
        });
    }

    private void showMotionToast(String title, String message, MotionToastStyle style) {
        MotionToast.Companion.darkColorToast(
                this,
                title,
                message,
                style,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
        );
    }
}
