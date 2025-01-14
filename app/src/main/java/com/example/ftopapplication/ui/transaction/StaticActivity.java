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
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.repository.TransactionRepository;
import com.example.ftopapplication.ui.home.HomeActivity;
import com.example.ftopapplication.viewmodel.transaction.StaticViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class StaticActivity extends AppCompatActivity {

    private TextView currentBalanceTextView, incomeTextView, expenseTextView;
    private LinearLayout transactionHistoryContainer;
    private BarChart moneyTrackerChart;
    private Button seeAllTransactionButton;
    private StaticViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);

        incomeTextView = findViewById(R.id.income_text);
        expenseTextView = findViewById(R.id.expense_text);
        currentBalanceTextView = findViewById(R.id.current_balance);



        int userId = getIntent().getIntExtra("user_id", -1);
        // Initialize components
        viewModel = new ViewModelProvider(this).get(StaticViewModel.class);
        viewModel.fetchUserWalletBalance(userId);

        moneyTrackerChart = findViewById(R.id.money_tracker_chart);
        transactionHistoryContainer = findViewById(R.id.transaction_history_container);
        seeAllTransactionButton = findViewById(R.id.btn_see_all_transactions);


        setupObservers();

        // Fetch all transactions for the user
        if (userId != -1) {
            viewModel.fetchTransactionSummary(userId);
        } else {
            showMotionToast("Error", "User ID not found!", MotionToastStyle.ERROR);
        }

        // Handle "See All Transactions" button
        seeAllTransactionButton.setOnClickListener(v -> openTransactionHistory(userId));
        setupBottomNavigation(userId);

    }

    private void setupObservers() {
        // Observe current balance
        viewModel.getBalanceLiveData().observe(this, balance -> {
            currentBalanceTextView.setText(String.format("$%,d", balance));
        });

        // Observe income
        viewModel.getIncomeLiveData().observe(this, income -> {
            incomeTextView.setText(String.format("$%,d", income));
        });

        // Observe expense
        viewModel.getExpenseLiveData().observe(this, expense -> {
            expenseTextView.setText(String.format("$%,d", expense));
        });

        // Observe transaction list
        viewModel.getTransactionsLiveData().observe(this, transactions -> {
            if (transactions == null || transactions.isEmpty()) {
                showMotionToast("Info", "No transactions available!", MotionToastStyle.INFO);
            } else {
                updateTransactionList(transactions);
                updateMoneyTrackerChart(transactions);
            }
        });

        // Observe error messages
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

    private void setupBottomNavigation(int userId) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                // Điều hướng đến HomeActivity
                Intent homeIntent = new Intent(StaticActivity.this, HomeActivity.class);
                homeIntent.putExtra("user_id", userId);
                startActivity(homeIntent);
                finish(); // Kết thúc StaticActivity
                return true;

            } else if (itemId == R.id.menu_cashflow) {
                // Đã ở StaticActivity, không cần làm gì
                return true;



            } else if (itemId == R.id.menu_message) {
                // Hiển thị thông báo cho mục Message
                Toast.makeText(this, "Message clicked", Toast.LENGTH_SHORT).show();
                return true;

            } else if (itemId == R.id.menu_profile) {
                // Hiển thị thông báo cho mục Profile
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
                return true;

            } else {
                return false;
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

        int userId = getIntent().getIntExtra("user_id", -1);
        for (Transaction transaction : transactions) {
            int dayOfWeek = getDayOfWeek(transaction.getTransactionDate()); // Convert date to day of week
            if (transaction.getReceiveUserId() == userId && transaction.isStatus()) {
                dailyIncome[dayOfWeek] += transaction.getTransactionAmount();
            } else if (transaction.getTransferUserId() == userId && transaction.isStatus()) {
                dailyExpense[dayOfWeek] += transaction.getTransactionAmount();
            }
        }

        for (int i = 0; i < 7; i++) {
            incomeEntries.add(new BarEntry(i, dailyIncome[i]));
            expenseEntries.add(new BarEntry(i, dailyExpense[i]));
        }

        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(Color.GREEN);

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(Color.RED);

        BarData barData = new BarData(incomeDataSet, expenseDataSet);
        barData.setBarWidth(0.3f);

        moneyTrackerChart.setData(barData);
        moneyTrackerChart.groupBars(0f, 0.2f, 0.02f);
        moneyTrackerChart.getDescription().setEnabled(false);
        moneyTrackerChart.invalidate();
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

    private void openTransactionHistory(int userId) {
        Intent intent = new Intent(StaticActivity.this, TransactionHistoryActivity.class);
        intent.putExtra("user_id", userId); // Truyền userId
        startActivity(intent); // Chuyển sang màn TransactionHistoryActivity
    }
}
