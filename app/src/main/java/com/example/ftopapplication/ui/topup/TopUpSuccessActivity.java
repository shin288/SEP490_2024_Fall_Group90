package com.example.ftopapplication.ui.topup;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.ftopapplication.data.model.BankTransfer;
import com.example.ftopapplication.R;
import com.example.ftopapplication.viewmodel.topup.TopUpViewModel;

public class TopUpSuccessActivity extends AppCompatActivity {

    private TextView statusMessage, amountText;
    private ImageView statusIcon;
    private Button doneButton;
    private TopUpViewModel viewModel;
    private Handler handler = new Handler();
    private Runnable statusChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_success);

        // Ánh xạ view
        statusMessage = findViewById(R.id.status_message);
        statusIcon = findViewById(R.id.status_icon);
        amountText = findViewById(R.id.topup_amount);
        doneButton = findViewById(R.id.done_button);

        // Nhận dữ liệu từ Intent
        int userId = getIntent().getIntExtra("user_id", -1);
        String amount = getIntent().getStringExtra("amount");

        amountText.setText("Amount: " + amount);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(TopUpViewModel.class);

        // Hiển thị trạng thái Pending ban đầu
        updateUIToPending();

        // Tự động kiểm tra trạng thái giao dịch sau mỗi 5 giây
        statusChecker = new Runnable() {
            @Override
            public void run() {
                viewModel.fetchBankTransfersByUserId(userId);
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(statusChecker);

        // Quan sát kết quả từ ViewModel
        viewModel.getBankTransfersLiveData().observe(this, transfers -> {
            boolean isSuccess = transfers.stream().anyMatch(BankTransfer::isStatus);
            if (isSuccess) {
                handler.removeCallbacks(statusChecker);
                updateUIToSuccess();
            }
        });

        // Nút hoàn thành
        doneButton.setOnClickListener(v -> finish());
    }

    private void updateUIToPending() {
        statusMessage.setText("Pending Approval");
        statusIcon.setImageResource(R.drawable.ic_pending);
        findViewById(R.id.white_card).setBackgroundColor(Color.YELLOW);
    }

    private void updateUIToSuccess() {
        statusMessage.setText("Top Up Success");
        statusIcon.setImageResource(R.drawable.ic_success);
        findViewById(R.id.white_card).setBackgroundColor(Color.GREEN);
    }
}
