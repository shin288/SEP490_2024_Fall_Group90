package com.example.ftopapplication.viewmodel.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSettingViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public ProfileSettingViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Gọi API lấy thông tin người dùng
    public void loadUserProfile(int userId) {
        userRepository.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userLiveData.postValue(response.body());
                } else {
                    errorLiveData.postValue("Failed to load user data");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }
}
