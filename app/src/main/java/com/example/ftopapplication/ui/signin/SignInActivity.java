package com.example.ftopapplication.ui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

import com.auth0.android.jwt.JWT;
import com.example.ftopapplication.R;
import com.example.ftopapplication.ui.home.HomeActivity;
import com.example.ftopapplication.data.repository.UserRepository;
import com.example.ftopapplication.ui.signup.SignUpActivity;
import com.example.ftopapplication.viewmodel.signin.SignInViewModel;
import com.example.ftopapplication.viewmodel.signin.SignInViewModelFactory;

public class SignInActivity extends AppCompatActivity {

    private EditText edtMail, edtPassword;
    private Button btnSignIn;
    private SignInViewModel signInViewModel;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Khởi tạo ViewModel
        signInViewModel = new ViewModelProvider(this, new SignInViewModelFactory(
                new UserRepository(), getSharedPreferences("UserSession", MODE_PRIVATE)
        )).get(SignInViewModel.class);

        // Ánh xạ các thành phần giao diện
        edtMail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUp = findViewById(R.id.tv_sign_up);

        // Chuyển sang màn hình Sign Up

        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Thiết lập sự kiện click nút "Sign In"
        btnSignIn.setOnClickListener(v -> {
            String emailPhone = edtMail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (emailPhone.isEmpty() || password.isEmpty()) {
                MotionToast.Companion.darkColorToast(this,
                        "Warning",
                        "Please fill all fields!",
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
            } else {
                signInViewModel.login(emailPhone, password);
            }
        });

        // Quan sát trạng thái đăng nhập
        observeViewModel();
    }

    private void observeViewModel() {
        // Quan sát trạng thái thành công
        signInViewModel.getLoginSuccess().observe(this, success -> {
            if (success) {
                MotionToast.Companion.darkColorToast(this,
                        "Success",
                        "Login Successfully!",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));

                // Lấy access_token từ SharedPreferences
                String accessToken = getSharedPreferences("UserSession", MODE_PRIVATE)
                        .getString("access_token", null);

                if (accessToken != null) {
                    // Decode token để lấy userId
                    JWT jwt = new JWT(accessToken);
                    int userId = jwt.getClaim("id").asInt();

                    // Chuyển sang màn hình chính và truyền userId qua Intent
                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                    finish();
                } else {
                    MotionToast.Companion.darkColorToast(this,"Error",
                            "Access token not found!",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
                }
            }
        });

        // Quan sát thông báo lỗi
        signInViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
