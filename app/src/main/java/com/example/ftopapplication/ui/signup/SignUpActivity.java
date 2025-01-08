package com.example.ftopapplication.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.UserRepository;
import com.example.ftopapplication.ui.signin.SignInActivity;
import com.example.ftopapplication.viewmodel.signup.SignUpViewModel;
import com.example.ftopapplication.viewmodel.signup.SignUpViewModelFactory;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone, edtPassword;
    private Button btnSignUp;
    private SignUpViewModel signUpViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpViewModel = new ViewModelProvider(this, new SignUpViewModelFactory(new UserRepository()))
                .get(SignUpViewModel.class);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                MotionToast.Companion.darkColorToast(this,
                        "Warning",
                        "Please fill in all required fields!",
                        MotionToastStyle.WARNING,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
                return;
            }

            // Tạo User và chuyển đến SetNewPinActivity
            User newUser = new User(email, name, phone, password, "https://example.com/avatar1.jpg", true);
            Intent intent = new Intent(SignUpActivity.this, SetNewPinActivity.class);
            intent.putExtra("user_data", newUser);
            startActivity(intent);
            finish();
        });


    }

    private void observeViewModel() {
        signUpViewModel.getSignUpSuccess().observe(this, success -> {
            if (success) {
                MotionToast.Companion.darkColorToast(this,
                        "Success",
                        "Account created successfully!",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));

                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
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
