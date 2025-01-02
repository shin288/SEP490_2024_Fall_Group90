package com.example.ftopapplication.viewmodel.signin;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.TokenResponse;
import com.example.ftopapplication.data.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public SignInViewModel(UserRepository userRepository, SharedPreferences sharedPreferences) {
        this.userRepository = userRepository;
        this.sharedPreferences = sharedPreferences;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void login(String email, String password) {
        Call<TokenResponse> call = userRepository.loginUser(email, password);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TokenResponse tokenResponse = response.body();

                    // Lưu access token vào SharedPreferences
                    sharedPreferences.edit().putString("access_token", tokenResponse.getAccessToken()).apply();

                    Log.d("SignInViewModel", "Access Token: " + tokenResponse.getAccessToken());
                    loginSuccess.postValue(true);
                } else {
                    errorMessage.postValue("Invalid email or password");
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.e("SignInViewModel", "Login failed: " + t.getMessage());
                errorMessage.postValue("An error occurred");
            }
        });
    }
}
