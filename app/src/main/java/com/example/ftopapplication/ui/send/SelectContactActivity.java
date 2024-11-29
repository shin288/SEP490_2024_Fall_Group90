package com.example.ftopapplication.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.UserRepository;
import com.example.ftopapplication.ui.pinentry.PinEntryActivity;
import com.example.ftopapplication.ui.send.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectContactActivity extends AppCompatActivity {

    private TextView tvAmount;
    private Button btnSend;
    private ImageView btnBack;
    private RecyclerView userList;
    private UserAdapter userAdapter;
    private List<User> users;

    private String selectedContactName = ""; // Tên người nhận
    private String selectedContactPhone = ""; // Số điện thoại người nhận
    private float amount = 0.0f; // Số tiền

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        // Ánh xạ các view
        tvAmount = findViewById(R.id.tv_amount);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        userList = findViewById(R.id.contact_list);

        userRepository = new UserRepository();

        // Lấy số tiền từ Intent
        amount = getIntent().getFloatExtra("amount", 0.0f);
        if (amount > 0) {
            tvAmount.setText(String.format("%.2f đ", amount)); // Hiển thị số tiền
        } else {
            Toast.makeText(this, "Không có số tiền hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnSend.setEnabled(false); // Không thể click
        btnSend.setBackgroundResource(R.drawable.button_continue_disabled);
        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút gửi
        btnSend.setOnClickListener(v -> {
            if (selectedContactName.isEmpty() || selectedContactPhone.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn một người nhận", Toast.LENGTH_SHORT).show();
            } else {
                navigateToPinEntry();
            }
        });

        // Thiết lập RecyclerView
        setupUserList();
    }

    private void setupUserList() {
        users = new ArrayList<>();

        // Khởi tạo adapter và gắn vào RecyclerView
        userAdapter = new UserAdapter(users);
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(userAdapter);

        // Lấy danh sách người dùng từ API
        fetchUsersFromApi();

        // Thêm sự kiện click vào item
        userAdapter.setOnUserClickListener(user -> {
            selectedContactName = user.getDisplayName(); // Lấy tên người nhận
            selectedContactPhone = user.getPhoneNumber(); // Lấy số điện thoại người nhận

            btnSend.setEnabled(true);
            btnSend.setBackgroundResource(R.drawable.button_background); // Thay đổi màu nút
        });
    }

    private void fetchUsersFromApi() {
        userRepository.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users.clear();
                    users.addAll(response.body());
                    userAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu trong adapter

                    Log.d("SelectContactActivity", "Fetched users: " + users.size());
                } else {
                    Toast.makeText(SelectContactActivity.this, "Không thể lấy danh sách người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(SelectContactActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToPinEntry() {
        Intent intent = new Intent(this, PinEntryActivity.class);
        intent.putExtra("receiver_name", selectedContactName); // Truyền tên người nhận
        intent.putExtra("receiver_phone", selectedContactPhone); // Truyền số điện thoại người nhận
        intent.putExtra("amount", amount); // Truyền số tiền
        startActivity(intent);
    }
}
