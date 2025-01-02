package com.example.ftopapplication.viewmodel.signin;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ftopapplication.data.repository.UserRepository;
import com.example.ftopapplication.viewmodel.signin.SignInViewModel;

public class SignInViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository userRepository;
    private final SharedPreferences sharedPreferences;

    public SignInViewModelFactory(UserRepository userRepository, SharedPreferences sharedPreferences) {
        this.userRepository = userRepository;
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SignInViewModel.class)) {
            return (T) new SignInViewModel(userRepository, sharedPreferences);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
