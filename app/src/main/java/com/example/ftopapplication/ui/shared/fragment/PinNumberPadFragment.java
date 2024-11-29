package com.example.ftopapplication.ui.shared.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ftopapplication.R;

public class PinNumberPadFragment extends Fragment {

    private OnPinNumberPadClickListener listener;

    public interface OnPinNumberPadClickListener {
        void onNumberClick(String number);
        void onBackspaceClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPinNumberPadClickListener) {
            listener = (OnPinNumberPadClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " phải triển khai OnPinNumberPadClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_pad_pin, container, false);

        // Thiết lập các sự kiện click cho các nút trong bàn phím PIN
        view.findViewById(R.id.btn_1).setOnClickListener(v -> listener.onNumberClick("1"));
        view.findViewById(R.id.btn_2).setOnClickListener(v -> listener.onNumberClick("2"));
        view.findViewById(R.id.btn_3).setOnClickListener(v -> listener.onNumberClick("3"));
        view.findViewById(R.id.btn_4).setOnClickListener(v -> listener.onNumberClick("4"));
        view.findViewById(R.id.btn_5).setOnClickListener(v -> listener.onNumberClick("5"));
        view.findViewById(R.id.btn_6).setOnClickListener(v -> listener.onNumberClick("6"));
        view.findViewById(R.id.btn_7).setOnClickListener(v -> listener.onNumberClick("7"));
        view.findViewById(R.id.btn_8).setOnClickListener(v -> listener.onNumberClick("8"));
        view.findViewById(R.id.btn_9).setOnClickListener(v -> listener.onNumberClick("9"));
        view.findViewById(R.id.btn_0).setOnClickListener(v -> listener.onNumberClick("0"));
        view.findViewById(R.id.btn_backspace).setOnClickListener(v -> listener.onBackspaceClick());

        return view;
    }

    public void setOnPinClickListener(OnPinNumberPadClickListener listener) {
        this.listener = listener;
    }
}
