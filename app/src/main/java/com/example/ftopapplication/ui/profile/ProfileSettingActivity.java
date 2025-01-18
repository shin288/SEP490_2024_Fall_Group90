package com.example.ftopapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.ui.signin.SignInActivity;
import com.example.ftopapplication.viewmodel.profile.ProfileSettingViewModel;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class ProfileSettingActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone;
    private Button btnSaveChange, btnLogout;
    private ProfileSettingViewModel viewModel;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        // Ánh xạ view
        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_phone);


        btnLogout = findViewById(R.id.btn_logout);

        // Lấy user_id từ Intent
        userId = getIntent().getIntExtra("user_id", -1);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(ProfileSettingViewModel.class);

        // Quan sát dữ liệu từ ViewModel
        setupObservers();

        // Gọi API lấy thông tin người dùng
        if (userId != -1) {
            viewModel.loadUserProfile(userId);
        } else {
            showMotionToast("Error", "Invalid user ID!", MotionToastStyle.ERROR);
        }

        // Xử lý sự kiện Logout
        btnLogout.setOnClickListener(v -> logoutUser());


    }

    private void setupObservers() {
        viewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                edtName.setText(user.getDisplayName());
                edtEmail.setText(user.getEmail());
                edtPhone.setText(user.getPhoneNumber());
            }
        });

        viewModel.getErrorLiveData().observe(this, errorMessage -> {
            showMotionToast("Error", errorMessage, MotionToastStyle.ERROR);
        });
    }


    private void logoutUser() {
        showMotionToast("Logout", "You have successfully logged out!", MotionToastStyle.SUCCESS);

        Intent intent = new Intent(ProfileSettingActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showMotionToast(String title, String message, MotionToastStyle style) {
        MotionToast.Companion.darkColorToast(
                this,
                title,
                message,
                style,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
        );
    }
}
