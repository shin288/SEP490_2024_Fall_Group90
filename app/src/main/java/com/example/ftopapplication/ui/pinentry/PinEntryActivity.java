package com.example.ftopapplication;

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

import com.example.ftopapplication.ui.send.SendSuccessActivity;
import com.example.ftopapplication.ui.topup.TopUpSuccessActivity;
import com.example.ftopapplication.ui.send.SelectContactActivity;
import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;
import com.example.ftopapplication.ui.topup.TopUpActivity;

public class PinEntryActivity extends AppCompatActivity implements NumberPadFragment.OnNumberPadClickListener {
    private View[] pinDots;
    private StringBuilder enteredPin = new StringBuilder();
    private TextView pinErrorMessage;
    private String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_entry);

        // Lấy tên màn hình gọi đến PinEntryActivity
        caller = getIntent().getStringExtra("caller");

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
            // Điều hướng đến ForgotPinActivity khi người dùng nhấn "Forgot Pin?"
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
                pinDots[i].setBackgroundResource(R.drawable.dot_filled);  // Hiển thị dấu chấm đã nhập
            } else {
                pinDots[i].setBackgroundResource(R.drawable.dot_empty);   // Hiển thị dấu chấm trống
            }
        }

        // Kiểm tra nếu đã nhập đủ 6 ký tự PIN
        if (enteredPin.length() == 6) {
            verifyPin();
        }
    }


    private void verifyPin() {
        String correctPin = "123456"; // Mã PIN đúng giả định
        if (enteredPin.toString().equals(correctPin)) {
            if ("SendActivity".equals(caller)) {
                // Kiểm tra số dư nếu đến từ SendActivity
                double balance = 5000.00;
                double amountToSend = 10000.00; // Giả sử số tiền cần gửi
                if (amountToSend > balance) {
                    // Hiển thị thông báo khi số dư không đủ
                    Toast.makeText(this, "Send failed, please try again later or check your connection", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    // Chuyển sang màn hình thành công khi gửi thành công
                    Intent intent = new Intent(this, SendSuccessActivity.class);
                    intent.putExtra("amount", "$" + amountToSend);
                    intent.putExtra("balance", "$" + balance);
                    startActivity(intent);
                    finish();
                }
            } else if ("TopUpActivity".equals(caller)) {
                // Thành công trong trường hợp Top-Up
                Intent intent = new Intent(this, TopUpSuccessActivity.class);
                intent.putExtra("amount", "$100.00"); // Giả sử số tiền nạp
                intent.putExtra("balance", "$5000.00"); // Giả sử số dư mới sau khi nạp
                startActivity(intent);
                finish();
            }
        } else {
            // Hiển thị lỗi mã PIN không đúng
            pinErrorMessage.setVisibility(View.VISIBLE); // Hiển thị TextView lỗi
            enteredPin.setLength(0); // Xóa mã PIN đã nhập
            updatePinDisplay();
        }
    }
}
