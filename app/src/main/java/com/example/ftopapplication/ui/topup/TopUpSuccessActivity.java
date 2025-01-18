package com.example.ftopapplication.ui.topup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.R;
import com.example.ftopapplication.ui.home.HomeActivity;
import com.example.ftopapplication.viewmodel.topup.TopUpViewModel;

public class TopUpSuccessActivity extends AppCompatActivity {

    private TextView statusMessage, amountText;
    private ImageView statusIcon;
    private Button doneButton;
    private TopUpViewModel viewModel;
    private Handler handler = new Handler();
    private Runnable statusChecker;
    private boolean isSuccessChecked = false;

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
        int walletUserId = getIntent().getIntExtra("walletUserId", -1);
        int transferId = getIntent().getIntExtra("transferId", -1);
        int amount = getIntent().getIntExtra("amount",0);

        amountText.setText("Amount: " + amount);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(TopUpViewModel.class);

        // Hiển thị trạng thái Pending ban đầu
        updateUIToSuccess();


        viewModel.getTransferStatusLiveData().observe(this, transfer -> {
            if (transfer != null) {
                Log.d("TopUpStatus", "Dữ liệu nhận được: " + transfer.toString());
                boolean status = transfer.isStatus();

                Log.d("TopUpStatus", "Status: " + transfer.isStatus());
                if (status && !isSuccessChecked) {
                    isSuccessChecked = true;
                    handler.removeCallbacks(statusChecker);
                    updateUIToSuccess();
                    Log.d("TopUpStatus", "Giao dịch thành công, đã dừng gọi API.");
                } else if (!status) {
                    updateUIToPending();
                }
            }
        });

        // Nút hoàn thành
        doneButton.setOnClickListener(v -> {
            Intent intent = new Intent(TopUpSuccessActivity.this, HomeActivity.class);
            intent.putExtra("user_id", walletUserId);  // Truyền walletUserId về HomeActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void updateUIToPending() {
        statusMessage.setText("Pending Approval");
        statusIcon.setImageResource(R.drawable.ic_pending);
        findViewById(R.id.white_card).setBackgroundColor(Color.YELLOW);
    }

    private void updateUIToSuccess() {
        statusMessage.setText("Top Up Success");
        statusIcon.setImageResource(R.drawable.ic_success);
        findViewById(R.id.white_card).setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(statusChecker);  // Dừng kiểm tra khi thoát màn hình
    }
}
