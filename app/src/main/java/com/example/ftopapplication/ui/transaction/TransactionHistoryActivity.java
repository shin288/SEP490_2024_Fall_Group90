package com.example.ftopapplication.ui.transaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.stream.Collectors;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.repository.TransactionRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity {

    private Button btnAll, btnIncome, btnExpense;
    private LinearLayout transactionListContainer;
    private TransactionRepository transactionRepository;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        btnAll = findViewById(R.id.btn_all);
        btnIncome = findViewById(R.id.btn_income);
        btnExpense = findViewById(R.id.btn_expense);
        transactionListContainer = findViewById(R.id.transaction_list_container);

        transactionRepository = new TransactionRepository();

        // Hiển thị tất cả giao dịch mặc định
        setActiveButton(btnAll);
        fetchTransactions("all");

        // Nút lọc
        btnAll.setOnClickListener(v -> {
            setActiveButton(btnAll);
            fetchTransactions("all");
        });

        btnIncome.setOnClickListener(v -> {
            setActiveButton(btnIncome);
            fetchTransactions("income");
        });

        btnExpense.setOnClickListener(v -> {
            setActiveButton(btnExpense);
            fetchTransactions("expense");
        });


        setContentView(R.layout.activity_transaction_history);

        btnBack = findViewById(R.id.btn_back); // Ánh xạ nút Back

        btnBack.setOnClickListener(v -> finish()); // Quay lại màn trước
    }

    private void fetchTransactions(String filterType) {
        int userId = 1; // Thay thế bằng ID người dùng thực tế
        transactionRepository.getAllTransactionsForUser(userId).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> transactions = response.body();
                    if ("income".equals(filterType)) {
                        updateTransactionList(filterTransactions(transactions, true));
                    } else if ("expense".equals(filterType)) {
                        updateTransactionList(filterTransactions(transactions, false));
                    } else {
                        updateTransactionList(transactions);
                    }
                } else {
                    Toast.makeText(TransactionHistoryActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(TransactionHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Transaction> filterTransactions(List<Transaction> transactions, boolean isIncome) {
        return transactions.stream()
                .filter(transaction -> transaction.isStatus() == isIncome)
                .collect(Collectors.toList());
    }


    private void updateTransactionList(List<Transaction> transactions) {
        transactionListContainer.removeAllViews(); // Xóa giao dịch cũ

        for (Transaction transaction : transactions) {
            View transactionItemView = LayoutInflater.from(this).inflate(R.layout.transaction_item, transactionListContainer, false);

            TextView titleTextView = transactionItemView.findViewById(R.id.transaction_title);
            TextView dateTextView = transactionItemView.findViewById(R.id.transaction_date);
            TextView amountTextView = transactionItemView.findViewById(R.id.transaction_amount);
            ImageView iconImageView = transactionItemView.findViewById(R.id.transaction_icon);

            // Thiết lập dữ liệu
            titleTextView.setText(transaction.getTransactionDescription());
            dateTextView.setText(transaction.getTransactionDate().toString()); // Định dạng nếu cần
            amountTextView.setText((transaction.isStatus() ? "+" : "-") + "$" + transaction.getTransactionAmount());
            amountTextView.setTextColor(transaction.isStatus() ? Color.parseColor("#008000") : Color.parseColor("#FF0000"));
            iconImageView.setImageResource(transaction.isStatus() ? R.drawable.ic_income : R.drawable.ic_expense);

            // Thêm vào danh sách
            transactionListContainer.addView(transactionItemView);
        }
    }

    private void setActiveButton(Button activeButton) {
        btnAll.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_background_disabled));
        btnIncome.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_background_disabled));
        btnExpense.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_background_disabled));

        btnAll.setTextColor(Color.parseColor("#888888"));
        btnIncome.setTextColor(Color.parseColor("#888888"));
        btnExpense.setTextColor(Color.parseColor("#888888"));

        activeButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_background));
        activeButton.setTextColor(Color.parseColor("#FFA500"));
    }

}
