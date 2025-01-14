package com.example.ftopapplication.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftopapplication.R;
import com.example.ftopapplication.adapter.UserAdapter;
import com.example.ftopapplication.ui.pinentry.PinEntryActivity;
import com.example.ftopapplication.viewmodel.send.SelectContactViewModel;

import java.util.ArrayList;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SelectContactActivity extends AppCompatActivity {

    private TextView tvAmount;
    private Button btnSend;
    private ImageView btnBack;
    private RecyclerView userList;
    private UserAdapter userAdapter;
    private SelectContactViewModel selectContactViewModel;
    private EditText searchContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        // Khởi tạo ViewModel
        selectContactViewModel = new ViewModelProvider(this).get(SelectContactViewModel.class);

        // Ánh xạ view
        tvAmount = findViewById(R.id.tv_amount);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        userList = findViewById(R.id.contact_list);
        searchContact = findViewById(R.id.search_contact);

        // Nhận dữ liệu từ Intent
        int senderUserId = getIntent().getIntExtra("user_id", -1);
        int amount = getIntent().getIntExtra("amount", 0);
        int balance = getIntent().getIntExtra("balance", 0);
        selectContactViewModel.setSenderUserId(senderUserId);
        selectContactViewModel.setAmount(amount);
        selectContactViewModel.setBalance(balance);

        // Quan sát dữ liệu
        selectContactViewModel.getAmount().observe(this, value -> tvAmount.setText(String.format("%d đ", value)));
        selectContactViewModel.getTransferSuccess().observe(this, success -> {
            if (success) {
                showMotionToast("Success", "Transfer successful!", MotionToastStyle.SUCCESS);
                navigateToPinEntry();
            } else {
                showMotionToast("Error", selectContactViewModel.getErrorMessage().getValue(), MotionToastStyle.ERROR);
            }
        });

        // Sự kiện quay lại
        btnBack.setOnClickListener(v -> finish());

        // Khởi tạo RecyclerView
        setupUserList();

        // Sự kiện gửi tiền
        btnSend.setOnClickListener(v -> {
            int receiverId = selectContactViewModel.getSelectedContactId().getValue();

            if (selectContactViewModel.getSenderUserId().getValue() == receiverId) {
                showMotionToast("Warning", "You cannot send money to yourself", MotionToastStyle.WARNING);
                return;
            }

            navigateToPinEntry();
        });

        // Sự kiện tìm kiếm
        setupSearchListener();
    }

    // Cài đặt RecyclerView
    private void setupUserList() {
        userAdapter = new UserAdapter(new ArrayList<>());
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(userAdapter);

        userAdapter.setOnUserClickListener(user -> {
            selectContactViewModel.setSelectedContactId(user.getId());
            btnSend.setEnabled(true);
        });

        selectContactViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                showMotionToast("Error", error, MotionToastStyle.ERROR);
            }
        });
    }

    // Sự kiện tìm kiếm người dùng
    private void setupSearchListener() {
        searchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchUsers(query);
                } else {
                    userAdapter.updateData(new ArrayList<>());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Gọi API tìm kiếm người dùng
    private void searchUsers(String query) {
        selectContactViewModel.searchUsers(query).observe(this, users -> {
            if (users != null && !users.isEmpty()) {
                userAdapter.updateData(users);
            } else {
                userAdapter.updateData(new ArrayList<>());
                showMotionToast("Info", "No users found", MotionToastStyle.INFO);
            }
        });
    }

    // Chuyển đến màn hình nhập PIN
    private void navigateToPinEntry() {
        int senderUserId = selectContactViewModel.getSenderUserId().getValue();
        int receiverUserId = selectContactViewModel.getSelectedContactId().getValue();
        int amount = selectContactViewModel.getAmount().getValue();
        int balance = selectContactViewModel.getBalance().getValue();
        boolean isTransfer = true;

        Intent intent = new Intent(this, PinEntryActivity.class);
        intent.putExtra("transfer_user_id", senderUserId);
        intent.putExtra("receiver_user_id", receiverUserId);
        intent.putExtra("amount", amount);
        intent.putExtra("balance", balance);
        intent.putExtra("is_transfer", isTransfer);
        startActivity(intent);
    }

    // Hiển thị MotionToast
    private void showMotionToast(String title, String message, MotionToastStyle style) {
        MotionToast.Companion.darkColorToast(this,
                title,
                message,
                style,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular));
    }
}
