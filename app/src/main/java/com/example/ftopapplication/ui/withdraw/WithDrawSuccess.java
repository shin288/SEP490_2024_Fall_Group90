package com.example.ftopapplication.ui.withdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ftopapplication.R;
import com.example.ftopapplication.ui.home.HomeActivity;

public class WithDrawSuccess extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_with_draw_success);

        // Nhận dữ liệu từ Intent
        int userId = getIntent().getIntExtra("user_id", -1);
        String bankName = getIntent().getStringExtra("bank_name");
        int accountNumber = getIntent().getIntExtra("account_number",-1);
        int amount = getIntent().getIntExtra("amount", 0);

        // Ánh xạ TextView
        TextView tvBankName = findViewById(R.id.bank_name);
        TextView tvAccountNumber = findViewById(R.id.account_number);
        TextView tvAmount = findViewById(R.id.withdraw_amount);
        Button btnDone = findViewById(R.id.done_button);

        // Hiển thị thông tin
        tvBankName.setText("Bank Name: " + bankName);
        tvAccountNumber.setText("Account Number: " + accountNumber);
        tvAmount.setText("Amount: $" + amount);

        // Xử lý sự kiện khi nhấn Done
        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(WithDrawSuccess.this, HomeActivity.class);
            intent.putExtra("user_id",userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();  // Đóng Activity hiện tại
        });
    }

}