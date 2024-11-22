package com.example.ftopapplication.network;

import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Endpoint lấy danh sách người dùng
    @GET("/users")
    Call<List<User>> getUsers();

    // Endpoint tìm kiếm người dùng theo tên
    @GET("/users/search")
    Call<List<User>> searchUsersByName(@Query("name") String name);

    // Endpoint tìm kiếm người dùng theo số điện thoại
    @GET("/users/search/phone")
    Call<User> searchUserByPhoneNumber(@Query("phone") String phoneNumber);

    // Endpoint lấy thông tin chi tiết người dùng theo ID
    @GET("/users/{id}")
    Call<User> getUserById(@Path("id") int userId);

    // Endpoint tạo giao dịch
    @POST("/transactions")
    Call<Void> createTransaction(@Body Transaction transaction);

    // Endpoint lấy danh sách giao dịch của một người dùng
    @GET("/transactions/user/{userId}")
    Call<List<Transaction>> getTransactionsByUserId(@Path("userId") int userId);

    // Endpoint lấy chi tiết giao dịch
    @GET("/transactions/{transactionId}")
    Call<Transaction> getTransactionById(@Path("transactionId") int transactionId);
}
