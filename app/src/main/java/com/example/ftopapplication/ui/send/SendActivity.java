package com.example.ftopapplication.ui.send;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;
import com.example.ftopapplication.R;

public class SendActivity extends AppCompatActivity implements NumberPadFragment.OnNumberPadClickListener {

    private TextView tvAmount;
    private Button btnContinue;
    private StringBuilder inputAmount = new StringBuilder("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        tvAmount = findViewById(R.id.tv_amount);
        btnContinue = findViewById(R.id.btn_continue);


        NumberPadFragment numberPadFragment = new NumberPadFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.number_pad_fragment, numberPadFragment);
        transaction.commit();


        findViewById(R.id.btn_100).setOnClickListener(v -> updateAmount("$100.00"));
        findViewById(R.id.btn_500).setOnClickListener(v -> updateAmount("$500.00"));
        findViewById(R.id.btn_1000).setOnClickListener(v -> updateAmount("$1000.00"));
        findViewById(R.id.btn_1500).setOnClickListener(v -> updateAmount("$1500.00"));


        btnContinue.setOnClickListener(v -> {

        });
    }


    private void updateAmount(String amount) {
        inputAmount = new StringBuilder(amount.replace("$", "").replace(",", "").trim());
        updateAmountDisplay();
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
        tvAmount.setText("$" + amount);

        boolean isAmountValid = !amount.equals("0.00");
        btnContinue.setEnabled(isAmountValid);
        btnContinue.setBackgroundResource(isAmountValid ? R.drawable.button_background : R.drawable.button_background_disabled);
    }
}
