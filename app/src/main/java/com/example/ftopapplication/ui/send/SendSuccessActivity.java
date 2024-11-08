package com.example.ftopapplication.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ftopapplication.R;

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
        String accountNumber = intent.getStringExtra("account_number");
        String time = intent.getStringExtra("time");

        // Gán dữ liệu vào các TextView
        tvAmount.setText(amount);
        tvName.setText(name);
        tvAccountNumber.setText(accountNumber);
        tvTime.setText(time);

        // Xử lý sự kiện nút "Done"
        Button doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(v -> finish());
    }
}
