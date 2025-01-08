package com.example.ftopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ftopapplication.ui.signup.SetNewPinActivity;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText otpDigit1, otpDigit2, otpDigit3, otpDigit4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        // Ánh xạ các ô nhập OTP
        otpDigit1 = findViewById(R.id.otp_digit_1);
        otpDigit2 = findViewById(R.id.otp_digit_2);
        otpDigit3 = findViewById(R.id.otp_digit_3);
        otpDigit4 = findViewById(R.id.otp_digit_4);

        // Nút quay lại
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Thêm TextWatcher cho từng ô nhập OTP
        setupOtpInputs();
    }

    private void setupOtpInputs() {
        otpDigit1.addTextChangedListener(new OtpTextWatcher(otpDigit1, otpDigit2));
        otpDigit2.addTextChangedListener(new OtpTextWatcher(otpDigit2, otpDigit3));
        otpDigit3.addTextChangedListener(new OtpTextWatcher(otpDigit3, otpDigit4));
        otpDigit4.addTextChangedListener(new OtpTextWatcher(otpDigit4, null));  // Ô cuối cùng không chuyển đến ô nào nữa
    }

    private void verifyOtp() {
        String otp = otpDigit1.getText().toString() +
                otpDigit2.getText().toString() +
                otpDigit3.getText().toString() +
                otpDigit4.getText().toString();

        if ("1234".equals(otp)) {  // Giả định OTP đúng là "1234"
            Intent intent = new Intent(OtpVerificationActivity.this, SetNewPinActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Mã OTP không đúng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            clearOtpFields();
        }
    }

    private void clearOtpFields() {
        otpDigit1.setText("");
        otpDigit2.setText("");
        otpDigit3.setText("");
        otpDigit4.setText("");
        otpDigit1.requestFocus();
    }

    private class OtpTextWatcher implements TextWatcher {
        private final EditText currentEditText;
        private final EditText nextEditText;

        OtpTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextEditText != null) {
                nextEditText.requestFocus();
            } else if (s.length() == 1 && nextEditText == null) {
                // Đã nhập đủ 4 ký tự, tự động kiểm tra OTP
                verifyOtp();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
