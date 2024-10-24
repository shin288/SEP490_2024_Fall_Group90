package com.example.ftopapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PaymentSuccessActivity extends AppCompatActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment_success);

        // action when click button "Done"
        findViewById(R.id.btn_done).setOnClickListener(v -> {
            // process when pay done
            finish(); //return screen
        });

        // action when click pay again
        findViewById(R.id.btn_pay_again).setOnClickListener(v -> {
            // process action when pay again
            // re open screen payment ...
        });
    }
}