package com.example.ftopapplication.ui.shared.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ftopapplication.R;

public class NumberPadFragment extends Fragment {

    private OnNumberPadClickListener listener;

    public interface OnNumberPadClickListener {
        void onNumberClick(String number);
        void onBackspaceClick();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnNumberPadClickListener) {
            listener = (OnNumberPadClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnNumberPadClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_pad, container, false);

        int[] numberButtonIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9
        };

        for (int id : numberButtonIds) {
            view.findViewById(id).setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNumberClick(((Button) v).getText().toString());
                }
            });
        }

        view.findViewById(R.id.btn_backspace).setOnClickListener(v -> {
            if (listener != null) {
                listener.onBackspaceClick();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
