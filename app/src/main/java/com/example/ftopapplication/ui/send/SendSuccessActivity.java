package com.example.ftopapplication.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.ftopapplication.R;
import com.example.ftopapplication.ui.home.HomeActivity;

public class SendSuccessActivity extends AppCompatActivity {

    private TextView tvAmount, tvName, tvAccountNumber, tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_success);

        tvAmount = findViewById(R.id.transfer_amount);

        tvTime = findViewById(R.id.transfer_time);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        int amount = intent.getIntExtra("amount", 0);
        String time = intent.getStringExtra("time");

        // Gán dữ liệu vào các TextView
        tvAmount.setText(String.format("%,d đ", amount));  // Hiển thị số tiền đúng định dạng
        tvTime.setText(time != null ? time : "N/A");

        // Nút "Done" quay lại Home
        Button doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(SendSuccessActivity.this, HomeActivity.class);
            int userId = getIntent().getIntExtra("user_id", -1);
            if (userId != -1) {
                homeIntent.putExtra("user_id", userId);
            }
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }
}
