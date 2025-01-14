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
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.ftopapplication.R;
import com.example.ftopapplication.ui.shared.fragment.NumberPadFragment;
import com.example.ftopapplication.viewmodel.topup.TopUpViewModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

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
            showMotionToast("Error", "Invalid user. Please log in again.", MotionToastStyle.ERROR);
            return;
        }

        topUpButton.setOnClickListener(v -> {
            String amountString = amountText.getText().toString().replace("đ", "").replace(",", "").trim();
            try {
                int amount = Integer.parseInt(amountString); // Chuyển đổi thành int
                if (amount > 0) {
                    viewModel.topUp(walletUserId, amount);
                } else {
                    showMotionToast("Warning", "Please enter a valid amount", MotionToastStyle.WARNING);
                }
            }catch (NumberFormatException e) {
                showMotionToast("Error", "Invalid amount format", MotionToastStyle.ERROR);
            }
        });

        observeViewModel(walletUserId);
    }

    private void observeViewModel(int userId) {
        viewModel.getQrCodeLiveData().observe(this, qrCode -> {
            if (qrCode != null) {
                showQrCodeDialog(qrCode,userId); // Hiển thị mã QR trong popup
            }
        });

        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                showMotionToast("Error", error, MotionToastStyle.ERROR);
            }
        });
    }

    private void showQrCodeDialog(String qrCodeContent, int userId) {
        // Tạo dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_top_up_qr_popup);

        // Gán view trong dialog
        ImageView qrCodeImage = dialog.findViewById(R.id.qrCodeImage);
        Button btnOkPopup = dialog.findViewById(R.id.btnOkPopup);
        Button btnClosePopup = dialog.findViewById(R.id.btnClosePopup);

        // Sử dụng ZXing để tạo mã QR
        try {
            int size = 500; // Kích thước mã QR
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    qrCodeContent, BarcodeFormat.QR_CODE, size, size);

            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrCodeImage.setImageBitmap(bitmap); // Hiển thị mã QR trong ImageView
        } catch (WriterException e) {
            e.printStackTrace();
            showMotionToast("Error", "Failed to generate QR Code", MotionToastStyle.ERROR);
        }

        // Xử lý nút "OK"
        btnOkPopup.setOnClickListener(v -> {
            dialog.dismiss();
            String amount = amountText.getText().toString();
            Intent intent = new Intent(TopUpActivity.this, TopUpSuccessActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("amount", amount);
            startActivity(intent);
            finish();
        });

        // Xử lý nút "Close"
        btnClosePopup.setOnClickListener(v -> {
            dialog.dismiss();
            showMotionToast("Cancelled", "Top Up Cancelled", MotionToastStyle.INFO);
        });

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
