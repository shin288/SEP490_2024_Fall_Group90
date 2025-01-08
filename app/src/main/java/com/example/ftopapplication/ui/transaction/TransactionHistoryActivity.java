package com.example.ftopapplication.ui.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.Transaction;
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

        // Lấy userId từ Intent
        userId = getIntent().getIntExtra("user_id", -1);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(TransactionHistoryViewModel.class);

        // Khởi tạo giao diện
        btnAll = findViewById(R.id.btn_all);
        btnIncome = findViewById(R.id.btn_income);
        btnExpense = findViewById(R.id.btn_expense);
        transactionListContainer = findViewById(R.id.transaction_list_container);
        btnBack = findViewById(R.id.btn_back);

        // Thiết lập nút Back
        btnBack.setOnClickListener(v -> finish());

        // Quan sát dữ liệu từ ViewModel
        setupObservers();

        // Mặc định hiển thị tất cả giao dịch
        setActiveButton(btnAll);
        viewModel.fetchAllTransactions(userId);

        // Thiết lập nút lọc giao dịch
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

    private void updateTransactionList(List<Transaction> transactions) {
        transactionListContainer.removeAllViews(); // Xóa danh sách cũ

        for (Transaction transaction : transactions) {
            View transactionItemView = LayoutInflater.from(this).inflate(R.layout.transaction_item, transactionListContainer, false);

            TextView titleTextView = transactionItemView.findViewById(R.id.transaction_title);
            TextView dateTextView = transactionItemView.findViewById(R.id.transaction_date);
            TextView amountTextView = transactionItemView.findViewById(R.id.transaction_amount);
            ImageView iconImageView = transactionItemView.findViewById(R.id.transaction_icon);

            // Thiết lập dữ liệu
            titleTextView.setText(transaction.getTransactionDescription());
            dateTextView.setText(transaction.getTransactionDate().toString());
            amountTextView.setText((transaction.getReceiveUserId() == userId ? "+" : "-") + "$" + transaction.getTransactionAmount());
            amountTextView.setTextColor(transaction.getReceiveUserId() == userId
                    ? getResources().getColor(R.color.income_color)
                    : getResources().getColor(R.color.expense_color));
            iconImageView.setImageResource(transaction.getReceiveUserId() == userId ? R.drawable.ic_income : R.drawable.ic_expense);

            // Thêm vào danh sách
            transactionListContainer.addView(transactionItemView);
        }
    }

    private void setActiveButton(Button activeButton) {
        btnAll.setBackgroundResource(R.drawable.rounded_button_background_disabled);
        btnIncome.setBackgroundResource(R.drawable.rounded_button_background_disabled);
        btnExpense.setBackgroundResource(R.drawable.rounded_button_background_disabled);

        btnAll.setTextColor(getResources().getColor(R.color.disabled_text_color));
        btnIncome.setTextColor(getResources().getColor(R.color.disabled_text_color));
        btnExpense.setTextColor(getResources().getColor(R.color.disabled_text_color));

        activeButton.setBackgroundResource(R.drawable.rounded_button_background);
        activeButton.setTextColor(getResources().getColor(R.color.active_text_color));
    }
}
