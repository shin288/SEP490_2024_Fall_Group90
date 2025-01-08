package com.example.ftopapplication.ui.signup;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.UserRepository;
import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;
import com.example.ftopapplication.ui.shared.fragment.PinNumberPadFragment;
import com.example.ftopapplication.viewmodel.signup.SignUpViewModel;
import com.example.ftopapplication.viewmodel.signup.SignUpViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ConfirmPinActivity extends AppCompatActivity implements PinNumberPadFragment.OnPinNumberPadClickListener {

    private List<View> pinDots = new ArrayList<>();
    private StringBuilder enteredPin = new StringBuilder();
    private String newPin;
    private User user;
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pin);

        // Retrieve User and PIN from Intent
        newPin = getIntent().getStringExtra("new_pin");
        user = (User) getIntent().getSerializableExtra("user_data");

        // Initialize ViewModel
        signUpViewModel = new ViewModelProvider(this, new SignUpViewModelFactory(new UserRepository()))
                .get(SignUpViewModel.class);

        // Map dots representing PIN
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
            confirmPin();
        }
    }

    private void confirmPin() {
        if (enteredPin.toString().equals(newPin)) {
            if (enteredPin.length() == 6) {
                user.setPin(Integer.parseInt(newPin)); // Gán mã PIN vào User
                sendUserToAPI(user);
            } else {
                MotionToast.Companion.darkColorToast(this,
                        "Error",
                        "PIN must be 6 digits long!",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        } else {
            MotionToast.Companion.darkColorToast(this,
                    "Error",
                    "PIN does not match. Please try again.",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
            enteredPin.setLength(0);
            updatePinDisplay();
        }
    }

    private void sendUserToAPI(User user) {

        // Chuyển int pin thành String trước khi gửi
        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("avatar", user.getAvatar());
        userPayload.put("displayName", user.getDisplayName());
        userPayload.put("email", user.getEmail());
        userPayload.put("id", user.getId());
        userPayload.put("isActive", user.isActive());
        userPayload.put("password", user.getPassword());
        userPayload.put("phoneNumber", user.getPhoneNumber());
        userPayload.put("pin", String.valueOf(user.getPin())); // Chuyển pin thành String
        userPayload.put("walletBalance", user.getWalletBalance());

        signUpViewModel.registerUserWithPayload(userPayload);

        signUpViewModel.getSignUpSuccess().observe(this, success -> {
            if (success) {
                MotionToast.Companion.darkColorToast(this,
                        "Success",
                        "Account and PIN set successfully!",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
                finish();
            }
        });

        signUpViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                MotionToast.Companion.darkColorToast(this,
                        "Error",
                        error,
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
            }
        });
    }

}
