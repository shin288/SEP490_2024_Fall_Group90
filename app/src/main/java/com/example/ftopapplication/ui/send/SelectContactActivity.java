package com.example.ftopapplication.ui.send;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ftopapplication.R;
import com.example.ftopapplication.data.model.Contact;
import com.example.ftopapplication.ui.send.adapter.ContactAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class SelectContactActivity extends AppCompatActivity {

    private TextView tvAmount;
    private TextView categoryText;
    private Button btnSend;
    private RecyclerView contactList;
    private ContactAdapter contactAdapter;
    private List<Contact> contacts;
    private String selectedContactName = "";
    private String selectedAccountNumber = "";
    private String amount = "$10,000.00"; // Thay bằng số tiền thực từ người dùng nhập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        tvAmount = findViewById(R.id.tv_amount);
        categoryText = findViewById(R.id.category_text);
        btnSend = findViewById(R.id.btn_send);
        contactList = findViewById(R.id.contact_list);

        // Lấy số tiền từ Intent và hiển thị
        amount = getIntent().getStringExtra("amount");
        tvAmount.setText(amount);

        // Hiển thị BottomSheet cho "Select Category"
        findViewById(R.id.select_category).setOnClickListener(v -> showCategoryBottomSheet());

        // Cài đặt nút "Send"
        btnSend.setOnClickListener(v -> {
            List<Contact> selectedContacts = contactAdapter.getSelectedContacts();
            if (selectedContacts.isEmpty()) {
                Toast.makeText(this, "Please select at least one contact", Toast.LENGTH_SHORT).show();
            } else {
                Contact selectedContact = selectedContacts.get(0); // Lấy liên hệ đầu tiên được chọn
                selectedContactName = selectedContact.getName();
                selectedAccountNumber = selectedContact.getPhoneNumber();

                // Chuyển đến SendSuccessActivity
                navigateToSendSuccess();
            }
        });

        // Thiết lập RecyclerView cho danh sách liên hệ
        setupContactList();
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

    private void setupContactList() {
        contacts = new ArrayList<>();
        // Tạo dữ liệu giả lập cho danh sách liên hệ
        contacts.add(new Contact("Ronaldo", "0318-1608-2105"));
        contacts.add(new Contact("Messi", "087562729264"));
        contacts.add(new Contact("Công Phượng", "087562729265"));
        contacts.add(new Contact("Quang Hải", "087562729266"));

        // Khởi tạo adapter và thiết lập cho RecyclerView
        contactAdapter = new ContactAdapter(this, contacts);
        contactList.setLayoutManager(new LinearLayoutManager(this));
        contactList.setAdapter(contactAdapter);

        // Lắng nghe thay đổi danh sách liên hệ được chọn và cập nhật nút Send
        contactAdapter.setOnSelectedContactsChangeListener(selectedCount -> {
            btnSend.setEnabled(selectedCount > 0);
            btnSend.setBackgroundResource(selectedCount > 0 ? R.drawable.button_background : R.drawable.button_background_disabled);
        });
    }

    private void navigateToSendSuccess() {
        Intent intent = new Intent(this, SendSuccessActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("name", selectedContactName);
        intent.putExtra("account_number", selectedAccountNumber);
        intent.putExtra("time", "3:02 PM"); // Bạn có thể đặt giờ động dựa trên thời gian thực
        startActivity(intent);
    }
}
