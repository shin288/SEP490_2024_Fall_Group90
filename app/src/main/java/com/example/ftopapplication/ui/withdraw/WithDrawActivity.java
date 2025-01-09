package com.example.ftopapplication.ui.withdraw;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.R;
import com.example.ftopapplication.viewmodel.withdraw.WithDrawViewModel;

public class WithDrawActivity extends AppCompatActivity {

    private EditText etBankName, etAccountNumber, etAmount;
    private Button btnWithdraw;
    private WithDrawViewModel withDrawViewModel;

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
        etBankName = findViewById(R.id.et_bank_name);
        etAccountNumber = findViewById(R.id.et_account_number);
        etAmount = findViewById(R.id.et_amount);
        btnWithdraw = findViewById(R.id.btn_withdraw);

        // Initialize ViewModel
        withDrawViewModel = new ViewModelProvider(this).get(WithDrawViewModel.class);

        // Observe success status
        withDrawViewModel.getIsSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "Withdraw successful!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, withDrawViewModel.getErrorMessage().getValue(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up button click listener
        btnWithdraw.setOnClickListener(v -> {
            String bankName = etBankName.getText().toString().trim();
            String accountNumber = etAccountNumber.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (bankName.isEmpty() || accountNumber.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = getIntent().getIntExtra("userId", -1);
            if (userId == -1) {
                Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set data in ViewModel
            withDrawViewModel.setBankName(bankName);
            withDrawViewModel.setAccountNumber(accountNumber);
            withDrawViewModel.setAmount(amount);

            // Perform withdraw
            withDrawViewModel.performWithdraw(userId);
        });
    }
}
