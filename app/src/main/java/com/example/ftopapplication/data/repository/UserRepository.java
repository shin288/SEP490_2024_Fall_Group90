package com.example.ftopapplication.data.repository;

import com.example.ftopapplication.TokenResponse;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public class UserRepository {

    private final ApiService apiService;

    public UserRepository() {
        apiService = ApiClient.getApiService();
    }

    // API để lấy danh sách tất cả người dùng
    public Call<List<User>> getAllUsers() {
        return apiService.getUsers();
    }

    // API để lấy thông tin chi tiết của một người dùng theo ID
    public Call<User> getUserById(int userId) {
        return apiService.getUserById(userId);
    }

    // API để tìm kiếm người dùng theo tên
    public Call<List<User>> searchUsersByName(String name) {
        return apiService.searchUsersByName(name);
    }

    // API để tìm kiếm người dùng theo số điện thoại
    public Call<User> searchUserByPhoneNumber(String phoneNumber) {
        return apiService.searchUserByPhoneNumber(phoneNumber);
    }

    public Call<TokenResponse> loginUser(String emailOrPhone, String password) {
        return apiService.loginUser(emailOrPhone, password);
    }

    public Call<User> registerUser(User user) {
        return apiService.registerUser(user);
    }

    public Call<Void> registerUserWithPayload(Map<String, Object> userPayload) {
        return apiService.registerUserWithPayload(userPayload);
    }

    public void getFilteredUsers(UserCallback callback) {
        apiService.getUsers().enqueue(new retrofit2.Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Lọc những user có đủ thông tin
                    List<User> filteredUsers = response.body().stream()
                            .filter(user -> user.getDisplayName() != null && !user.getDisplayName().isEmpty() &&
                                    user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
                            .collect(Collectors.toList());
                    callback.onSuccess(filteredUsers);
                } else {
                    callback.onError(new Throwable("Failed to fetch users"));
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public interface UserCallback {
        void onSuccess(List<User> users);
        void onError(Throwable throwable);
    }



}
