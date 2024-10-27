package com.example.ftopapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SummaryTransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_transaction);

        // action when click button "Proceed to Pay"
        findViewById(R.id.btn_changepw).setOnClickListener(v -> {
            // action pay in here
        });
    }
}