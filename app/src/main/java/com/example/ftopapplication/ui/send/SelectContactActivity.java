package com.example.ftopapplication.ui.send;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectContactActivity extends AppCompatActivity {

    private TextView tvAmount;
    private TextView categoryText;
    private Button btnSend;
    private ImageView btnBack;
    private RecyclerView userList;
    private UserAdapter userAdapter;
    private List<User> users;
    private String selectedContactName = "";
    private String selectedAccountNumber = "";
    private String selectedIdentifiedCode = "";
    private float amount = 0.0f; // Số tiền chuyển từ Intent (float)

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        // Ánh xạ các view
        tvAmount = findViewById(R.id.tv_amount);
        categoryText = findViewById(R.id.category_text);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        userList = findViewById(R.id.contact_list);

        userRepository = new UserRepository();

        // Lấy số tiền từ Intent
        amount = getIntent().getFloatExtra("amount", 0.0f);
        if (amount > 0) {
            tvAmount.setText(String.format("%.2f đ", amount)); // Hiển thị số tiền với định dạng float
        } else {
            Toast.makeText(this, "No amount provided", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình nếu không có số tiền
        }

        // Sự kiện nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Hiển thị BottomSheet để chọn danh mục
        findViewById(R.id.select_category).setOnClickListener(v -> showCategoryBottomSheet());

        // Cài đặt nút "Send"
        btnSend.setOnClickListener(v -> {
            if (selectedIdentifiedCode.isEmpty()) {
                Toast.makeText(this, "Please select a contact", Toast.LENGTH_SHORT).show();
            } else {
                navigateToPinEntry();
            }
        });

        // Thiết lập RecyclerView
        setupUserList();
    }

    private void showCategoryBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_category);

        bottomSheetDialog.findViewById(R.id.food_drink).setOnClickListener(v -> {
            categoryText.setText("Food & Drink");
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.findViewById(R.id.supermarket).setOnClickListener(v -> {
            categoryText.setText("Supermarket");
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.findViewById(R.id.salon).setOnClickListener(v -> {
            categoryText.setText("Salon & Nail");
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.findViewById(R.id.others).setOnClickListener(v -> {
            categoryText.setText("Others");
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void setupUserList() {
        users = new ArrayList<>();

        // Khởi tạo adapter và gắn vào RecyclerView
        userAdapter = new UserAdapter(users);
        userAdapter.setOnUserClickListener(user -> {
            selectedContactName = user.getName();
            selectedAccountNumber = String.valueOf(user.getPhoneNumber());
            selectedIdentifiedCode = String.valueOf(user.getIdentifiedCode());
            btnSend.setEnabled(true);
            btnSend.setBackgroundResource(R.drawable.button_background); // Thay đổi màu nút
        });

        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(userAdapter);

        // Lấy danh sách người dùng từ API
        fetchUsersFromApi();
    }

    private void fetchUsersFromApi() {
        userRepository.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users.clear();
                    users.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SelectContactActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(SelectContactActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToPinEntry() {
        Intent intent = new Intent(this, PinEntryActivity.class);
        intent.putExtra("identified_code", selectedIdentifiedCode); // Truyền mã định danh
        intent.putExtra("amount", amount); // Truyền số tiền (float)
        startActivity(intent);
    }
}
