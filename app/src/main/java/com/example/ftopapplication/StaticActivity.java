package com.example.ftopapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ftopapplication.ui.transaction.TransactionHistoryActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StaticActivity extends AppCompatActivity {

    private LinearLayout transactionHistoryContainer;
    private BarChart moneyTrackerChart;
    private Button seeAllTransactionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);

        // Initialize BarChart for Money Tracker
        moneyTrackerChart = findViewById(R.id.money_tracker_chart);
        setupMoneyTrackerChart();

        // Initialize transaction history container
        transactionHistoryContainer = findViewById(R.id.transaction_history_container);

        // Add transaction items
        addTransactionItem("Cashback Promo", "1 Jan 2020", "+$16,000", R.drawable.ic_cashback, "#00FF00");
        addTransactionItem("Food Delivery", "1 Jan 2020", "-$56,000", R.drawable.ic_food, "#FF0000");
        addTransactionItem("Money Received", "1 Jan 2020", "+$1,056,000", R.drawable.ic_money_received, "#00FF00");

        // Initialize "See All Transaction" button
        seeAllTransactionButton = findViewById(R.id.btn_see_all_transactions);
        seeAllTransactionButton.setOnClickListener(v -> openTransactionHistory());
    }

    // Set up the Money Tracker chart with sample data
    private void setupMoneyTrackerChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(1, 10000));
        entries.add(new BarEntry(2, 5000));
        entries.add(new BarEntry(3, 7000));
        entries.add(new BarEntry(4, 2000));
        entries.add(new BarEntry(5, 3000));
        entries.add(new BarEntry(6, 6000));
        entries.add(new BarEntry(7, 4000));

        BarDataSet dataSet = new BarDataSet(entries, "Income and Expense");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(dataSet);
        moneyTrackerChart.setData(data);
        moneyTrackerChart.getDescription().setEnabled(false);
        moneyTrackerChart.invalidate();
    }

    // Add a transaction item to the transaction history container
    private void addTransactionItem(String title, String date, String amount, int iconRes, String color) {
        // Inflate transaction_item.xml and add it to the container
        View transactionItemView = LayoutInflater.from(this).inflate(R.layout.transaction_item, transactionHistoryContainer, false);

        // Set data for transaction item
        TextView titleTextView = transactionItemView.findViewById(R.id.transaction_title);
        TextView dateTextView = transactionItemView.findViewById(R.id.transaction_date);
        TextView amountTextView = transactionItemView.findViewById(R.id.transaction_amount);
        ImageView iconImageView = transactionItemView.findViewById(R.id.transaction_icon);

        titleTextView.setText(title);
        dateTextView.setText(date);
        amountTextView.setText(amount);
        amountTextView.setTextColor(Color.parseColor(color));
        iconImageView.setImageResource(iconRes);

        // Add transaction item to container
        transactionHistoryContainer.addView(transactionItemView);
    }

    // Method to open the TransactionHistoryActivity
    private void openTransactionHistory() {
        Intent intent = new Intent(StaticActivity.this, TransactionHistoryActivity.class);
        startActivity(intent);
    }
}
