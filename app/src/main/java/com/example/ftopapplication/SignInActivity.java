package com.example.ftopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.UserRepository;
import com.example.ftopapplication.ui.home.HomeActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private EditText edtMail, edtPassword;
    private Button btnSignIn;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Khởi tạo UserRepository
        userRepository = new UserRepository();

        // Ánh xạ các thành phần giao diện
        edtMail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);

        // Xử lý sự kiện đăng nhập
        btnSignIn.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String emailPhone = edtMail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (emailPhone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng User để gửi lên API
        User loginUser = new User();
        loginUser.setEmail(emailPhone); // hoặc loginUser.setPhoneNumber() nếu dùng số điện thoại
        loginUser.setPassword(password);

        // Gửi yêu cầu đăng nhập
        Call<User> call = userRepository.loginUser(loginUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    // Đăng nhập thành công
                    Toast.makeText(SignInActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();

                    // Chuyển sang màn hình chính
                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignInActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("SignInActivity", "Error: " + t.getMessage());
                Toast.makeText(SignInActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
