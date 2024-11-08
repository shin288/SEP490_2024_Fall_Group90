package com.example.ftopapplication.ui.topup;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ftopapplication.R;
import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;

public class TopUpActivity extends AppCompatActivity implements NumberPadFragment.OnNumberPadClickListener {

    private TextView amountText;
    private StringBuilder inputAmount = new StringBuilder("0.00");
    private Button topUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        amountText = findViewById(R.id.tv_amount);
        topUpButton = findViewById(R.id.btn_top_up);

        topUpButton.setOnClickListener(v -> {
            String amount = amountText.getText().toString();
            if (!amount.equals("$0.00")) {
                Toast.makeText(this, "Top Up Successful: " + amount, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter an amount to top up", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNumberClick(String number) {
        if (inputAmount.toString().equals("0.00")) {
            inputAmount = new StringBuilder(number);
        } else {
            inputAmount.append(number);
        }
        updateAmountDisplay();
    }

    @Override
    public void onBackspaceClick() {
        if (inputAmount.length() > 1) {
            inputAmount.deleteCharAt(inputAmount.length() - 1);
        } else {
            inputAmount = new StringBuilder("0.00");
        }
        updateAmountDisplay();
    }

    private void updateAmountDisplay() {
        String amount = inputAmount.toString();
        if (amount.length() > 2) {
            amount = new StringBuilder(amount).insert(amount.length() - 2, ".").toString();
        }
        amountText.setText("$" + amount);
    }
}

