package com.example.ftopapplication.ui.transaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.repository.TransactionRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaticActivity extends AppCompatActivity {

    private LinearLayout transactionHistoryContainer;
    private BarChart moneyTrackerChart;
    private Button seeAllTransactionButton;
    private TransactionRepository transactionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);

        // Initialize components
        moneyTrackerChart = findViewById(R.id.money_tracker_chart);
        transactionHistoryContainer = findViewById(R.id.transaction_history_container);
        seeAllTransactionButton = findViewById(R.id.btn_see_all_transactions);

        transactionRepository = new TransactionRepository();

        // Fetch all transactions for the user
        int userId = 1; // Replace with actual user ID
        fetchAllTransactionsForUser(userId);

        // Handle "See All Transactions" button
        seeAllTransactionButton.setOnClickListener(v -> openTransactionHistory());
    }

    private void fetchAllTransactionsForUser(int userId) {
        transactionRepository.getAllTransactionsForUser(userId).enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Transaction> transactions = response.body();
                    updateTransactionList(transactions);
                    updateMoneyTrackerChart(transactions);
                } else {
                    Toast.makeText(StaticActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(StaticActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTransactionList(List<Transaction> transactions) {
        transactionHistoryContainer.removeAllViews(); // Clear previous items

        for (Transaction transaction : transactions) {
            String title = transaction.getTransactionDescription();
            String date = transaction.getTransactionDate().toString(); // Format if needed
            String amount = (transaction.isStatus() ? "+" : "-") + "$" + transaction.getTransactionAmount();
            String color = transaction.isStatus() ? "#00FF00" : "#FF0000"; // Green for income, red for expense
            int iconRes = transaction.isStatus() ? R.drawable.ic_income : R.drawable.ic_expense;

            addTransactionItem(title, date, amount, iconRes, color);
        }
    }

    private int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 là Chủ Nhật
    }

    private void updateMoneyTrackerChart(List<Transaction> transactions) {
        ArrayList<BarEntry> incomeEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();

        float[] dailyIncome = new float[7];
        float[] dailyExpense = new float[7];

        for (Transaction transaction : transactions) {
            int dayOfWeek = getDayOfWeek(transaction.getTransactionDate()); // Convert date to day of week
            if (transaction.isStatus()) {
                dailyIncome[dayOfWeek] += transaction.getTransactionAmount();
            } else {
                dailyExpense[dayOfWeek] += transaction.getTransactionAmount();
            }
        }

        for (int i = 0; i < 7; i++) {
            incomeEntries.add(new BarEntry(i, dailyIncome[i]));
            expenseEntries.add(new BarEntry(i, dailyExpense[i]));
        }

        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(Color.parseColor("#00BFFF"));

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(Color.parseColor("#FF4500"));

        BarData barData = new BarData(incomeDataSet, expenseDataSet);
        barData.setBarWidth(0.4f);

        moneyTrackerChart.setData(barData);
        moneyTrackerChart.groupBars(-0.5f, 0.2f, 0.02f);
        moneyTrackerChart.getDescription().setEnabled(false);
        moneyTrackerChart.invalidate();
    }

    private int getDayOfWeek(String date) {
        // Replace with actual implementation to parse date
        return 0; // Mocked as Sunday
    }

    private void addTransactionItem(String title, String date, String amount, int iconRes, String color) {
        View transactionItemView = LayoutInflater.from(this).inflate(R.layout.transaction_item, transactionHistoryContainer, false);

        TextView titleTextView = transactionItemView.findViewById(R.id.transaction_title);
        TextView dateTextView = transactionItemView.findViewById(R.id.transaction_date);
        TextView amountTextView = transactionItemView.findViewById(R.id.transaction_amount);
        ImageView iconImageView = transactionItemView.findViewById(R.id.transaction_icon);

        titleTextView.setText(title);
        dateTextView.setText(date);
        amountTextView.setText(amount);
        amountTextView.setTextColor(Color.parseColor(color));
        iconImageView.setImageResource(iconRes);

        transactionHistoryContainer.addView(transactionItemView);
    }

    private void openTransactionHistory() {
        Intent intent = new Intent(StaticActivity.this, TransactionHistoryActivity.class);
        startActivity(intent); // Chuyển sang màn TransactionHistoryActivity
    }
}
