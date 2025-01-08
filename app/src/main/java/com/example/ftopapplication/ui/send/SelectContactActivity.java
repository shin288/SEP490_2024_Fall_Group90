package com.example.ftopapplication.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftopapplication.R;
import com.example.ftopapplication.adapter.UserAdapter;
import com.example.ftopapplication.ui.pinentry.PinEntryActivity;
import com.example.ftopapplication.viewmodel.send.SelectContactViewModel;

import java.util.ArrayList;

public class SelectContactActivity extends AppCompatActivity {

    private TextView tvAmount;
    private Button btnSend;
    private ImageView btnBack;
    private RecyclerView userList;
    private UserAdapter userAdapter;
    private SelectContactViewModel selectContactViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        // Initialize ViewModel
        selectContactViewModel = new ViewModelProvider(this).get(SelectContactViewModel.class);

        tvAmount = findViewById(R.id.tv_amount);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        userList = findViewById(R.id.contact_list);

        // Get data from Intent
        int senderUserId = getIntent().getIntExtra("userId", -1);
        int amount = getIntent().getIntExtra("amount", 0);
        selectContactViewModel.setSenderUserId(senderUserId);
        selectContactViewModel.setAmount(amount);

        // Observe data
        selectContactViewModel.getAmount().observe(this, value -> tvAmount.setText(String.format("%,d đ", value)));
        selectContactViewModel.getTransferSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Transfer successful!", Toast.LENGTH_SHORT).show();
                navigateToPinEntry();
            } else {
                Toast.makeText(this, selectContactViewModel.getErrorMessage().getValue(), Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        setupUserList();

        // Handle Send button
        btnSend.setOnClickListener(v -> {
            int receiverId = selectContactViewModel.getSelectedContactId().getValue();
            if (senderUserId == receiverId) {
                Toast.makeText(this, "You cannot send money to yourself", Toast.LENGTH_SHORT).show();
            } else {
                selectContactViewModel.performTransfer(senderUserId, receiverId, amount);
            }
        });
    }

    private void setupUserList() {
        userAdapter = new UserAdapter(new ArrayList<>());
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(userAdapter);

        // Observe user list
        selectContactViewModel.fetchUsers();
        selectContactViewModel.getUsers().observe(this, users -> {
            userAdapter.updateData(users);
            userAdapter.setOnUserClickListener(user -> {
                selectContactViewModel.setSelectedContactId(user.getId());
                btnSend.setEnabled(true);
            });
        });
    }

    private void navigateToPinEntry() {
        Intent intent = new Intent(this, PinEntryActivity.class);
        intent.putExtra("transfer_user_id", selectContactViewModel.getSenderUserId().getValue());
        intent.putExtra("receiver_user_id", selectContactViewModel.getSelectedContactId().getValue());
        intent.putExtra("amount", selectContactViewModel.getAmount().getValue());
        startActivity(intent);
    }
}
