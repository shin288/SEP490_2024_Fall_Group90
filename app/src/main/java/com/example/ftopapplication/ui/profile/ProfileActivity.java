package com.example.ftopapplication.ui.profile;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.ftopapplication.R;
import com.example.ftopapplication.viewmodel.profile.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel profileViewModel;
    private EditText etName, etEmail, etPhone, etBirthday;
    private ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        // Khởi tạo ViewModel
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Lấy dữ liệu người dùng (giả sử userId là 1)
        profileViewModel.loadProfile(1);

        // Quan sát dữ liệu từ ViewModel và cập nhật UI
        profileViewModel.getProfileData().observe(this, user -> {
            if (user != null) {
                etName.setText(user.getDisplayName());
                etEmail.setText(user.getEmail());
                etPhone.setText(user.getPhoneNumber());

            }
        });
    }
}
