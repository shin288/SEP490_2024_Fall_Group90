package com.example.ftopapplication.data.repository;

import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.network.ApiClient;
import com.example.ftopapplication.network.ApiService;

import java.util.List;

import retrofit2.Call;

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

    public Call<User> loginUser(User user){
        return apiService.loginUser(user);
    }
}
