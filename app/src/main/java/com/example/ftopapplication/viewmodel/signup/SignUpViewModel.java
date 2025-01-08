package com.example.ftopapplication.viewmodel.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.UserRepository;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Boolean> signUpSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public SignUpViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Boolean> getSignUpSuccess() {
        return signUpSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void registerUser(User user) {
        userRepository.registerUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    signUpSuccess.postValue(true);
                } else {
                    errorMessage.postValue("Register failed! Please check again all information.");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                errorMessage.postValue("Error connections: " + t.getMessage());
            }
        });

    }

    public void registerUserWithPayload(Map<String, Object> userPayload) {
        userRepository.registerUserWithPayload(userPayload).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    signUpSuccess.postValue(true);
                } else {
                    errorMessage.postValue("Registration failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.postValue("Connection error: " + t.getMessage());
            }
        });
    }




}
