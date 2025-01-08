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
        tvName = findViewById(R.id.destination_name);
        tvAccountNumber = findViewById(R.id.destination_account_number);
        tvTime = findViewById(R.id.transfer_time);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String amount = intent.getStringExtra("amount");
        String name = intent.getStringExtra("name");

        String time = intent.getStringExtra("time");

        // Gán dữ liệu vào các TextView
        tvAmount.setText(amount);
        tvName.setText(name);

        tvTime.setText(time);

        // Xử lý sự kiện nút "Done"
        Button doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(SendSuccessActivity.this, HomeActivity.class);
            int userId = getIntent().getIntExtra("user_id", -1);
            if (userId != -1) {
                homeIntent.putExtra("user_id", userId);
            } else {
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            }
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa stack
            startActivity(homeIntent);
            finish(); // Kết thúc SendSuccessActivity
        });
    }
}
