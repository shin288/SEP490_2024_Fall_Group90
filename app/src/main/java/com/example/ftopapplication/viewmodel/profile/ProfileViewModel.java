package com.example.ftopapplication.viewmodel.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<User> profileData = new MutableLiveData<>();

    public ProfileViewModel() {
        repository = new UserRepository();
    }

    public LiveData<User> getProfileData() {
        return profileData;
    }

    public void loadProfile(int userId) {
        repository.getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    profileData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Xử lý lỗi
                profileData.setValue(null);
            }
        });
    }
}
