package com.example.ftopapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;

import java.util.ArrayList;
import java.util.List;

public class SetNewPinActivity extends AppCompatActivity implements NumberPadFragment.OnNumberPadClickListener {

    private List<View> pinDots = new ArrayList<>();
    private StringBuilder enteredPin = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_pin);

        // Ánh xạ các dot đại diện cho ký tự PIN
        pinDots.add(findViewById(R.id.dot1));
        pinDots.add(findViewById(R.id.dot2));
        pinDots.add(findViewById(R.id.dot3));
        pinDots.add(findViewById(R.id.dot4));
        pinDots.add(findViewById(R.id.dot5));
        pinDots.add(findViewById(R.id.dot6));

        // Thêm NumberPadFragment vào activity
        NumberPadFragment numberPadFragment = new NumberPadFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.number_pad_fragment, numberPadFragment);
        transaction.commit();
    }

    // Xử lý khi nhấn số trên NumberPadFragment
    @Override
    public void onNumberClick(String number) {
        if (enteredPin.length() < 6) {
            enteredPin.append(number);
            updatePinDisplay();
        }
    }

    // Xử lý khi nhấn phím xóa
    @Override
    public void onBackspaceClick() {
        if (enteredPin.length() > 0) {
            enteredPin.deleteCharAt(enteredPin.length() - 1);
            updatePinDisplay();
        }
    }

    // Cập nhật giao diện hiển thị mã PIN
    private void updatePinDisplay() {
        for (int i = 0; i < pinDots.size(); i++) {
            if (i < enteredPin.length()) {
                pinDots.get(i).setBackgroundResource(R.drawable.dot_filled);  // Hiển thị dấu chấm đã nhập
            } else {
                pinDots.get(i).setBackgroundResource(R.drawable.dot_empty);   // Hiển thị dấu chấm trống
            }
        }

        // Kiểm tra nếu đã nhập đủ 6 ký tự PIN
        if (enteredPin.length() == 6) {
            saveNewPin();
        }
    }

    // Lưu mã PIN mới
    private void saveNewPin() {
        Toast.makeText(this, "PIN mới đã được thiết lập!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
