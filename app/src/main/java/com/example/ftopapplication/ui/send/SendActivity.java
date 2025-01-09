package com.example.ftopapplication.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.R;
import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;
import com.example.ftopapplication.viewmodel.send.SendViewModel;

public class SendActivity extends AppCompatActivity implements NumberPadFragment.OnNumberPadClickListener {

    private TextView tvAmount;
    private Button btnContinue;
    private SendViewModel sendViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // Initialize ViewModel
        sendViewModel = new ViewModelProvider(this).get(SendViewModel.class);

        int userId = getIntent().getIntExtra("user_id", -1);
        int balance = getIntent().getIntExtra("balance", 0);
        sendViewModel.setUserId(userId); // Lưu vào ViewModel
        sendViewModel.setUserBalance(balance);


        tvAmount = findViewById(R.id.tv_amount);
        btnContinue = findViewById(R.id.btn_continue);

        // Observe LiveData
        sendViewModel.getAmount().observe(this, amount -> {
            updateAmountDisplay(amount);
            btnContinue.setEnabled(amount > 0 && amount <= 1_000_000_000);
        });

        // Initialize NumberPadFragment
        NumberPadFragment numberPadFragment = new NumberPadFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.number_pad_fragment, numberPadFragment);
        transaction.commit();

        // Setup Preset Amount Buttons
        findViewById(R.id.btn_1000).setOnClickListener(v -> sendViewModel.setAmount(1000));
        findViewById(R.id.btn_5000).setOnClickListener(v -> sendViewModel.setAmount(5000));
        findViewById(R.id.btn_10000).setOnClickListener(v -> sendViewModel.setAmount(10000));
        findViewById(R.id.btn_20000).setOnClickListener(v -> sendViewModel.setAmount(20000));
        findViewById(R.id.btn_50000).setOnClickListener(v -> sendViewModel.setAmount(50000));

        // Handle Continue Button
        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectContactActivity.class);
            intent.putExtra("amount", sendViewModel.getAmount().getValue());
            intent.putExtra("user_id", userId);
            intent.putExtra("balance", sendViewModel.getUserBalance().getValue());
            startActivity(intent);
        });
    }

    private void updateAmountDisplay(int amount) {
        tvAmount.setText(String.format("%d đ", amount));
    }

    @Override
    public void onNumberClick(String number) {
        sendViewModel.appendAmount(number);
    }

    @Override
    public void onBackspaceClick() {
        sendViewModel.removeLastDigit();
    }
}
