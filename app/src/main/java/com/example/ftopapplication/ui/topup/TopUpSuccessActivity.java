package com.example.ftopapplication.ui.topup;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ftopapplication.R;

public class TopUpSuccessActivity extends AppCompatActivity {

    private TextView amountText;
    private TextView balanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_success);

        TextView amountText = findViewById(R.id.topup_amount);

        Button okButton = findViewById(R.id.done_button);

        String amount = getIntent().getStringExtra("amount");
        String balance = getIntent().getStringExtra("balance");

        amountText.setText(amount);
        balanceText.setText(balance);

        okButton.setOnClickListener(v -> finish());
    }
}
