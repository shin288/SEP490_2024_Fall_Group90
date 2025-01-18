package com.example.ftopapplication.ui.transaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.BankTransfer;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.ui.home.HomeActivity;
import com.example.ftopapplication.ui.send.SendSuccessActivity;
import com.example.ftopapplication.ui.store.StoreDetailActivity;
import com.example.ftopapplication.ui.topup.TopUpSuccessActivity;
import com.example.ftopapplication.ui.withdraw.WithDrawSuccess;
import com.example.ftopapplication.viewmodel.transaction.TransactionHistoryViewModel;

import java.util.List;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class TransactionHistoryActivity extends AppCompatActivity {

    private Button btnAll, btnIncome, btnExpense;
    private LinearLayout transactionListContainer;
    private ImageView btnBack;
    private TransactionHistoryViewModel viewModel;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        userId = getIntent().getIntExtra("user_id", -1);
        viewModel = new ViewModelProvider(this).get(TransactionHistoryViewModel.class);

        btnAll = findViewById(R.id.btn_all);
        btnIncome = findViewById(R.id.btn_income);
        btnExpense = findViewById(R.id.btn_expense);
        transactionListContainer = findViewById(R.id.transaction_list_container);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> backToHome());

        setupObservers();

        setActiveButton(btnAll);
        viewModel.fetchAllTransactions(userId);

        btnAll.setOnClickListener(v -> {
            setActiveButton(btnAll);
            viewModel.filterTransactions("all", userId);
        });

        btnIncome.setOnClickListener(v -> {
            setActiveButton(btnIncome);
            viewModel.filterTransactions("income", userId);
        });

        btnExpense.setOnClickListener(v -> {
            setActiveButton(btnExpense);
            viewModel.filterTransactions("expense", userId);
        });
    }

    private void setupObservers() {
        viewModel.getTransactionsLiveData().observe(this, transactions -> {
            if (transactions != null && !transactions.isEmpty()) {
                updateTransactionList(transactions);
            } else {
                showMotionToast("Info", "No transactions available!", MotionToastStyle.INFO);
            }
        });

        viewModel.getErrorLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                showMotionToast("Error", errorMessage, MotionToastStyle.ERROR);
            }
        });
    }

    private void showMotionToast(String title, String message, MotionToastStyle style) {
        MotionToast.Companion.darkColorToast(this,
                title,
                message,
                style,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
    }

    private void updateTransactionList(List<Object> transactions) {
        transactionListContainer.removeAllViews();

        for (Object item : transactions) {
            View transactionItemView = LayoutInflater.from(this).inflate(R.layout.transaction_item, transactionListContainer, false);

            TextView titleTextView = transactionItemView.findViewById(R.id.transaction_title);
            TextView dateTextView = transactionItemView.findViewById(R.id.transaction_date);
            TextView amountTextView = transactionItemView.findViewById(R.id.transaction_amount);
            ImageView iconImageView = transactionItemView.findViewById(R.id.transaction_icon);

            if (item instanceof Transaction) {
                Transaction transaction = (Transaction) item;
                titleTextView.setText(transaction.getTransactionDescription());
                dateTextView.setText(transaction.getTransactionDate().toString());
                amountTextView.setText((transaction.getReceiveUserId() == userId ? "+" : "-")  + transaction.getTransactionAmount() + " d");
                iconImageView.setImageResource(transaction.getReceiveUserId() == userId ? R.drawable.ic_income : R.drawable.ic_expense);

                transactionItemView.setOnClickListener(v -> openTransactionDetail(transaction));

            } else if (item instanceof BankTransfer) {
                BankTransfer transfer = (BankTransfer) item;
                String type = (transfer.getTransferType() ? "Top-up" : "Withdraw");
                titleTextView.setText(type);
                dateTextView.setText(transfer.getTransferDate().toString());
                amountTextView.setText((transfer.getTransferType() ? "+" : "-")  + transfer.getAmount() + " d");
                iconImageView.setImageResource(transfer.getTransferType() ? R.drawable.ic_income : R.drawable.ic_expense);

                transactionItemView.setOnClickListener(v -> openBankTransferDetail(transfer));
            }

            transactionListContainer.addView(transactionItemView);
        }
    }

    private void openTransactionDetail(Transaction transaction) {
        Intent intent;
        String description = transaction.getTransactionDescription().toLowerCase();

        if (description.contains("transfer via app") || description.contains("transfer for order ")) {
            intent = new Intent(this, SendSuccessActivity.class);
            intent.putExtra("amount", transaction.getTransactionAmount());
            intent.putExtra("time", transaction.getTransactionDate().toString());
        } else {
            showMotionToast("Error", "No detailed view for this transaction.", MotionToastStyle.ERROR);
            return;
        }

        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    private void openBankTransferDetail(BankTransfer transfer) {
        Intent intent;

        if (transfer.getTransferType()) { // Top-up
            intent = new Intent(this, TopUpSuccessActivity.class);
            intent.putExtra("walletUserId", transfer.getWalletUserId());
            intent.putExtra("transferId", transfer.getTransferId());
            intent.putExtra("amount", transfer.getAmount());
        } else { // Withdraw
            intent = new Intent(this, WithDrawSuccess.class);
            intent.putExtra("amount", transfer.getAmount());
            intent.putExtra("bank_name", transfer.getBankName());
            intent.putExtra("account_number", transfer.getAccountNumber());
        }

        intent.putExtra("user_id", userId);
        startActivity(intent);
    }


    private void backToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
        finish();
    }

    private void setActiveButton(Button activeButton) {
        btnAll.setBackgroundResource(R.drawable.rounded_button_background_disabled);
        btnIncome.setBackgroundResource(R.drawable.rounded_button_background_disabled);
        btnExpense.setBackgroundResource(R.drawable.rounded_button_background_disabled);

        activeButton.setBackgroundResource(R.drawable.rounded_button_background);
    }
}
