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
import com.example.ftopapplication.adapter.UserAdapter;

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

    private String selectedContactName = ""; // Recipient's name
    private String selectedContactPhone = ""; // Recipient's phone number
    private int transferUserId = 4; // Default sender ID
    private int selectedContactId = -1; // Recipient's ID
    private float transferUserBalance = 0.0f; // Sender's balance
    private float amount = 0.0f; // Transaction amount

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        // Map views
        tvAmount = findViewById(R.id.tv_amount);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        userList = findViewById(R.id.contact_list);

        userRepository = new UserRepository();

        // Get the amount from the Intent
        amount = getIntent().getFloatExtra("amount", 0.0f);
        if (amount > 0) {
            tvAmount.setText(String.format("%.2f đ", amount)); // Display the amount
        } else {
            Toast.makeText(this, "No valid amount specified", Toast.LENGTH_SHORT).show();
            finish(); // End the activity if no amount is provided
        }

        // Fetch sender's balance from the API
        fetchTransferUserBalance();

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Send button
        btnSend.setEnabled(false); // Disable send button initially
        btnSend.setBackgroundResource(R.drawable.button_continue_disabled);
        btnSend.setOnClickListener(v -> {

            if (selectedContactId == transferUserId) {
                Toast.makeText(this, "You cannot send money to yourself", Toast.LENGTH_SHORT).show();
            } else if (selectedContactName.isEmpty() || selectedContactPhone.isEmpty()) {
                Toast.makeText(this, "Please select a recipient", Toast.LENGTH_SHORT).show();
            } else {
                navigateToPinEntry(); // Navigate to the PIN entry screen
            }
        });

        // Set up RecyclerView
        setupUserList();
    }

    private void setupUserList() {
        users = new ArrayList<>();

        // Initialize the adapter and attach it to the RecyclerView
        userAdapter = new UserAdapter(users);
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(userAdapter);

        // Fetch users from the API
        fetchUsersFromApi();

        // Item click event
        userAdapter.setOnUserClickListener(user -> {
            selectedContactName = user.getDisplayName(); // Get recipient's name
            selectedContactPhone = user.getPhoneNumber(); // Get recipient's phone number
            selectedContactId = user.getId(); // Get recipient's ID


            // Enable send button and update button UI
            btnSend.setEnabled(true);
            btnSend.setBackgroundResource(R.drawable.button_background); // Update button color
        });
    }

    private void fetchUsersFromApi() {
        userRepository.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    users.clear();
                    users.addAll(response.body());
                    userAdapter.notifyDataSetChanged(); // Update data in adapter

                    Log.d("SelectContactActivity", "Fetched users: " + users.size());
                } else {
                    Toast.makeText(SelectContactActivity.this, "Unable to fetch user list", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(SelectContactActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTransferUserBalance() {
        userRepository.getUserById(transferUserId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    transferUserBalance = response.body().getWalletBalance(); // Get balance from API
                    Log.d("SelectContactActivity", "Sender's balance: " + transferUserBalance);
                } else {
                    Toast.makeText(SelectContactActivity.this, "Unable to fetch sender's information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(SelectContactActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateToPinEntry() {


        Intent intent = new Intent(this, PinEntryActivity.class);
        intent.putExtra("transfer_user_id", transferUserId); // Pass sender ID
        intent.putExtra("receiver_user_id", selectedContactId); // Key cho Receiver User ID
        intent.putExtra("receiver_name", selectedContactName); // Pass recipient's name
        intent.putExtra("receiver_phone", selectedContactPhone); // Pass recipient's phone number
        intent.putExtra("current_balance", transferUserBalance); // Pass sender's balance
        intent.putExtra("amount", amount); // Pass amount
        startActivity(intent);
    }
}
