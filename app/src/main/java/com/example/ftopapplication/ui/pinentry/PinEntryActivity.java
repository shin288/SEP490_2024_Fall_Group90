package com.example.ftopapplication.ui.pinentry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.ftopapplication.ForgotPinActivity;
import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.UserRepository;
import com.example.ftopapplication.ui.send.SelectContactActivity;
import com.example.ftopapplication.ui.send.SendSuccessActivity;
import com.example.ftopapplication.ui.topup.TopUpSuccessActivity;
import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;
import com.example.ftopapplication.ui.topup.TopUpActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PinEntryActivity extends AppCompatActivity implements NumberPadFragment.OnNumberPadClickListener {
    private View[] pinDots;
    private StringBuilder enteredPin = new StringBuilder();
    private TextView pinErrorMessage;
    private String caller;
    private double amountToSend;
    private double userBalance;
    private int userId; // ID người dùng để lấy mã PIN từ API

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_entry);

        // Lấy thông tin Intent
        caller = getIntent().getStringExtra("caller");
        amountToSend = getIntent().getDoubleExtra("amount", 0.0); // Số tiền cần gửi
        userBalance = getIntent().getDoubleExtra("balance", 0.0); // Số dư tài khoản người dùng
        userId = getIntent().getIntExtra("user_id", -1); // ID người dùng

        userRepository = new UserRepository();

        // Thiết lập padding cho hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ các dot để hiển thị trạng thái mã PIN
        pinDots = new View[]{
                findViewById(R.id.dot1),
                findViewById(R.id.dot2),
                findViewById(R.id.dot3),
                findViewById(R.id.dot4),
                findViewById(R.id.dot5),
                findViewById(R.id.dot6)
        };

        // Ánh xạ TextView hiển thị lỗi khi nhập sai PIN
        pinErrorMessage = findViewById(R.id.pin_error_message);

        // Thiết lập nút quay lại
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            if ("SendActivity".equals(caller)) {
                startActivity(new Intent(this, SelectContactActivity.class));
            } else if ("TopUpActivity".equals(caller)) {
                startActivity(new Intent(this, TopUpActivity.class));
            }
            finish();
        });

        // Xử lý sự kiện "Forgot Pin?"
        TextView forgotPinText = findViewById(R.id.tv_forgot_pin);
        forgotPinText.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPinActivity.class));
        });

        // Thêm NumberPadFragment vào PinEntryActivity
        NumberPadFragment numberPadFragment = new NumberPadFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.number_pad_fragment, numberPadFragment);
        transaction.commit();
    }

    @Override
    public void onNumberClick(String number) {
        if (enteredPin.length() < 6) {
            enteredPin.append(number);
            updatePinDisplay();
        }
    }

    @Override
    public void onBackspaceClick() {
        if (enteredPin.length() > 0) {
            enteredPin.deleteCharAt(enteredPin.length() - 1);
            updatePinDisplay();
        }
    }

    private void updatePinDisplay() {
        for (int i = 0; i < pinDots.length; i++) {
            if (i < enteredPin.length()) {
                pinDots[i].setBackgroundResource(R.drawable.dot_filled); // Hiển thị dấu chấm đã nhập
            } else {
                pinDots[i].setBackgroundResource(R.drawable.dot_empty); // Hiển thị dấu chấm trống
            }
        }

        // Kiểm tra nếu đã nhập đủ 6 ký tự PIN
        if (enteredPin.length() == 6) {
            verifyPin();
        }
    }

    private void verifyPin() {
        userRepository.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String correctPin = response.body().getPassword(); // Mã PIN từ API
                    if (enteredPin.toString().equals(correctPin)) {
                        handleCorrectPin();
                    } else {
                        displayPinError();
                    }
                } else {
                    Toast.makeText(PinEntryActivity.this, "Failed to verify PIN. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(PinEntryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCorrectPin() {
        if ("SendActivity".equals(caller)) {
            if (amountToSend > userBalance) {
                Toast.makeText(this, "Insufficient balance for this transaction.", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, SendSuccessActivity.class);
                intent.putExtra("amount", amountToSend);
                intent.putExtra("remaining_balance", userBalance - amountToSend);
                startActivity(intent);
                finish();
            }
        } else if ("TopUpActivity".equals(caller)) {
            Intent intent = new Intent(this, TopUpSuccessActivity.class);
            intent.putExtra("amount", amountToSend);
            intent.putExtra("new_balance", userBalance + amountToSend);
            startActivity(intent);
            finish();
        }
    }

    private void displayPinError() {
        pinErrorMessage.setVisibility(View.VISIBLE); // Hiển thị lỗi mã PIN không đúng
        enteredPin.setLength(0); // Xóa mã PIN đã nhập
        updatePinDisplay();
    }
}
