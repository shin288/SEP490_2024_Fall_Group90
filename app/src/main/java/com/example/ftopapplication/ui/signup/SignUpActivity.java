package com.example.ftopapplication.ui.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.UserRepository;
import com.example.ftopapplication.ui.signin.SignInActivity;
import com.example.ftopapplication.viewmodel.signup.SignUpViewModel;
import com.example.ftopapplication.viewmodel.signup.SignUpViewModelFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPhone, edtPassword;
    private Button btnSignUp;
    private SignUpViewModel signUpViewModel;
    private TextView tvSignIn;

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
        tvSignIn = findViewById(R.id.SignIn);

        btnSignUp.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                showToast("Warning", "Please fill in all required fields!", MotionToastStyle.WARNING);
                return;
            }

            if (!isValidEmail(email)) {
                showToast("Invalid Email", "Email must be in correct format and end with @fpt.edu.vn", MotionToastStyle.ERROR);
                return;
            }

            if (!isValidPhone(phone)) {
                showToast("Invalid Phone", "Phone number must be exactly 10 digits.", MotionToastStyle.ERROR);
                return;
            }

            if (password.length() < 8) {
                showToast("Invalid Password", "Password must be at least 8 characters.", MotionToastStyle.ERROR);
                return;
            }

            // Kiểm tra trùng email/phone trước khi đăng ký
            checkDuplicateAndRegister(name, email, phone, password);
        });

        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    private boolean isValidEmail(String email) {
        // Regex kiểm tra trước @ không có ký tự đặc biệt hoặc dấu cách
        String emailRegex = "^[A-Za-z0-9._%+-]+@fpt\\.edu\\.vn$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private void showToast(String title, String message, MotionToastStyle style) {
        MotionToast.Companion.darkColorToast(this,
                title,
                message,
                style,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
    }

    private void observeViewModel() {
        signUpViewModel.getSignUpSuccess().observe(this, success -> {
            if (success) {
                showToast("Success", "Account created successfully!", MotionToastStyle.SUCCESS);
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUpViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                showToast("Error", error, MotionToastStyle.ERROR);
            }
        });
    }

    private void checkDuplicateAndRegister(String name, String email, String phone, String password) {
        // Gọi API kiểm tra dữ liệu trùng
        String url = "http://10.0.2.2:8000/api/user/check-duplicate?email=" + email + "&phone=" + phone;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean exists = response.getBoolean("exists");
                        if (exists) {
                            showToast("Error", "Email or Phone number already exists!", MotionToastStyle.ERROR);
                        } else {
                            moveToConfirmPinActivity(name, email, phone, password);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("Error", "Server error. Please try again.", MotionToastStyle.ERROR);
                    }
                },
                error -> showToast("Error", "Failed to connect to server.", MotionToastStyle.ERROR)
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void moveToConfirmPinActivity(String name, String email, String phone, String password) {
        User newUser = new User(email, name, phone, password, "https://example.com/avatar1.jpg", true);
        Intent intent = new Intent(SignUpActivity.this, SetNewPinActivity.class);
        intent.putExtra("user_data", newUser);
        startActivity(intent);
    }


}
