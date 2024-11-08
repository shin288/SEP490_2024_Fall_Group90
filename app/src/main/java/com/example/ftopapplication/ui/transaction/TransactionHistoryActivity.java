package com.example.ftopapplication.ui.transaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ftopapplication.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class TransactionHistoryActivity extends AppCompatActivity {

    private Button btnAll, btnIncome, btnExpense;
    private ImageView btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        btnAll = findViewById(R.id.btn_all);
        btnIncome = findViewById(R.id.btn_income);
        btnExpense = findViewById(R.id.btn_expense);
        btnFilter = findViewById(R.id.btn_filter);

        // Thiết lập mặc định ban đầu là hiển thị tất cả giao dịch
        setActiveButton(btnAll);
        showAllTransactions();

        // Logic cho các nút filter
        btnAll.setOnClickListener(v -> {
            setActiveButton(btnAll);
            showAllTransactions();
        });
        btnIncome.setOnClickListener(v -> {
            setActiveButton(btnIncome);
            showIncomeTransactions();
        });
        btnExpense.setOnClickListener(v -> {
            setActiveButton(btnExpense);
            showExpenseTransactions();
        });
        btnFilter.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void setActiveButton(Button activeButton) {
        // Đặt lại màu nền cho tất cả các nút
        btnAll.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_background_disabled));
        btnIncome.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_background_disabled));
        btnExpense.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_background_disabled));

        btnAll.setTextColor(Color.parseColor("#888888"));
        btnIncome.setTextColor(Color.parseColor("#888888"));
        btnExpense.setTextColor(Color.parseColor("#888888"));

        // Đặt màu nền cho nút đang được chọn
        activeButton.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_button_background));
        activeButton.setTextColor(Color.parseColor("#FFA500"));
    }

    private void showAllTransactions() {
        // Thực hiện hiển thị tất cả giao dịch
        // TODO: Thêm logic để hiển thị danh sách tất cả giao dịch
    }

    private void showIncomeTransactions() {
        // Thực hiện hiển thị các giao dịch thu nhập
        // TODO: Thêm logic để hiển thị danh sách giao dịch thu nhập
    }

    private void showExpenseTransactions() {
        // Thực hiện hiển thị các giao dịch chi tiêu
        // TODO: Thêm logic để hiển thị danh sách giao dịch chi tiêu
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog filterDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
        filterDialog.setContentView(view);

        TextView thisWeek = view.findViewById(R.id.filter_this_week);
        TextView thisMonth = view.findViewById(R.id.filter_this_month);
        TextView pastThreeMonths = view.findViewById(R.id.filter_past_three_months);
        TextView thisYear = view.findViewById(R.id.filter_this_year);
        TextView customDate = view.findViewById(R.id.filter_custom_date);

        // Set click listeners cho từng tuỳ chọn bộ lọc
        thisWeek.setOnClickListener(v -> {
            // Lọc theo tuần này
            filterDialog.dismiss();
        });

        thisMonth.setOnClickListener(v -> {
            // Lọc theo tháng này
            filterDialog.dismiss();
        });

        pastThreeMonths.setOnClickListener(v -> {
            // Lọc theo 3 tháng trước
            filterDialog.dismiss();
        });

        thisYear.setOnClickListener(v -> {
            // Lọc theo năm này
            filterDialog.dismiss();
        });

        customDate.setOnClickListener(v -> {
            // Mở bộ chọn ngày tuỳ chỉnh
            filterDialog.dismiss();
            showCustomDatePicker();
        });

        filterDialog.show();
    }

    private void showCustomDatePicker() {
        // Implement custom date picker dialog here
        // You can use a DatePickerDialog to select start and end dates
    }
}
