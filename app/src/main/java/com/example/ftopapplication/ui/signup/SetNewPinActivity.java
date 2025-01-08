package com.example.ftopapplication.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.ftopapplication.R;
import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;
import com.example.ftopapplication.ui.shared.fragment.PinNumberPadFragment;

import java.util.ArrayList;
import java.util.List;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SetNewPinActivity extends AppCompatActivity implements PinNumberPadFragment.OnPinNumberPadClickListener {

    private List<View> pinDots = new ArrayList<>();
    private StringBuilder enteredPin = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_pin);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());


        pinDots.add(findViewById(R.id.dot1));
        pinDots.add(findViewById(R.id.dot2));
        pinDots.add(findViewById(R.id.dot3));
        pinDots.add(findViewById(R.id.dot4));
        pinDots.add(findViewById(R.id.dot5));
        pinDots.add(findViewById(R.id.dot6));
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
        for (int i = 0; i < pinDots.size(); i++) {
            if (i < enteredPin.length()) {
                pinDots.get(i).setBackgroundResource(R.drawable.dot_filled);
            } else {
                pinDots.get(i).setBackgroundResource(R.drawable.dot_empty);
            }
        }

        if (enteredPin.length() == 6) {
            moveToConfirmPinActivity();
        }
    }

    private void moveToConfirmPinActivity() {
        MotionToast.Companion.darkColorToast(this,
                "Success",
                "PIN set successfully!",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));

            Intent intent = new Intent(SetNewPinActivity.this, ConfirmPinActivity.class);
            intent.putExtra("new_pin", enteredPin.toString());
            intent.putExtra("user_data", getIntent().getSerializableExtra("user_data")); // Chuyển User sang
            startActivity(intent);
            finish();
    }
}
