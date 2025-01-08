package com.example.ftopapplication.ui.topup;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.ftopapplication.R;
import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;
import com.example.ftopapplication.viewmodel.topup.TopUpViewModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class TopUpActivity extends AppCompatActivity implements NumberPadFragment.OnNumberPadClickListener {

    private TextView amountText;
    private StringBuilder inputAmount = new StringBuilder("0");
    private Button topUpButton;
    private TopUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        amountText = findViewById(R.id.tv_amount);
        topUpButton = findViewById(R.id.btn_top_up);

        viewModel = new ViewModelProvider(this).get(TopUpViewModel.class);

        int walletUserId = getIntent().getIntExtra("user_id", -1);
        if (walletUserId == -1) {
            Toast.makeText(this, "Invalid user. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        topUpButton.setOnClickListener(v -> {
            String amountString = amountText.getText().toString().replace("đ", "").replace(",", "").trim();
            try {
                int amount = Integer.parseInt(amountString); // Chuyển đổi thành int
                if (amount > 0) {
                    viewModel.topUp(walletUserId, amount);
                } else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            }
        });

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getQrCodeLiveData().observe(this, qrCode -> {
            if (qrCode != null) {
                showQrCodeDialog(qrCode); // Hiển thị mã QR trong popup
            }
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQrCodeDialog(String qrCodeContent) {
        // Tạo dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_top_up_qr_popup);

        // Gán view trong dialog
        ImageView qrCodeImage = dialog.findViewById(R.id.qrCodeImage);
        Button btnClosePopup = dialog.findViewById(R.id.btnClosePopup);

        // Sử dụng ZXing để tạo mã QR
        try {
            // Định cấu hình mã QR
            int size = 500; // Kích thước mã QR
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    qrCodeContent, BarcodeFormat.QR_CODE, size, size);

            // Chuyển đổi BitMatrix thành Bitmap
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            // Hiển thị mã QR trong ImageView
            qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR Code", Toast.LENGTH_SHORT).show();
        }

        // Đóng dialog khi nhấn "Close"
        btnClosePopup.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    @Override
    public void onNumberClick(String number) {
        if (inputAmount.toString().equals("0")) {
            inputAmount = new StringBuilder(number);
        } else {
            inputAmount.append(number);
        }
        updateAmountDisplay();
    }

    @Override
    public void onBackspaceClick() {
        if (inputAmount.length() > 1) {
            inputAmount.deleteCharAt(inputAmount.length() - 1);
        } else {
            inputAmount = new StringBuilder("0");
        }
        updateAmountDisplay();
    }

    private void updateAmountDisplay() {
        String amount = inputAmount.toString();
        if (amount.isEmpty() || amount.equals("0")) {
            amountText.setText("0 đ"); // Mặc định khi số tiền là 0
        } else {
            try {
                // Định dạng số tiền
                int parsedAmount = Integer.parseInt(amount);
                amountText.setText(String.format("%d đ", parsedAmount)); // Định dạng có thêm "đ"
            } catch (NumberFormatException e) {
                amountText.setText("0 đ"); // Lỗi định dạng -> hiển thị 0 đ
            }
        }
    }

}
