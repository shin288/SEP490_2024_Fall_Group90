package com.example.ftopapplication.network;

import com.example.ftopapplication.TokenResponse;
import com.example.ftopapplication.data.model.ApiResponse;
import com.example.ftopapplication.data.model.OrderTransactionRequest;
import com.example.ftopapplication.data.model.OrderTransactionResponse;
import com.example.ftopapplication.data.model.Product;
import com.example.ftopapplication.data.model.Store;
import com.example.ftopapplication.data.model.TopUpRequest;
import com.example.ftopapplication.data.model.TopUpResponse;
import com.example.ftopapplication.data.model.Transaction;
import com.example.ftopapplication.data.model.TransactionSummary;
import com.example.ftopapplication.data.model.User;
import com.example.ftopapplication.data.model.Voucher;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Endpoint lấy danh sách người dùng
    @GET("/api/user/findUser")
    Call<List<User>> getUsers();

    @POST("/api/user/registerUser")
    Call<User> registerUser(@Body User user);

    @POST("/api/user/registerUser")
    Call<Void> registerUserWithPayload(@Body Map<String, Object> userPayload);

    // Endpoint tìm kiếm người dùng theo tên
    @GET("/users/search")
    Call<List<User>> searchUsersByName(@Query("name") String name);

    // Endpoint tìm kiếm người dùng theo số điện thoại
    @GET("/users/search/phone")
    Call<User> searchUserByPhoneNumber(@Query("phone") String phoneNumber);

    // Endpoint lấy thông tin chi tiết người dùng theo ID
    @GET("/api/user/findUser/{id}")
    Call<User> getUserById(@Path("id") int userId);

    // Endpoint tạo giao dịch
    @POST("/transactions")
    Call<Void> createTransaction(@Body Transaction transaction);

    // Endpoint lấy chi tiết giao dịch
    @GET("/transactions/{transactionId}")
    Call<Transaction> getTransactionById(@Path("transactionId") int transactionId);

    // API chuyển tiền
    @POST("api/transaction/transfer")
    Call<Transaction> transferMoney(@Body Transaction transaction);

    @GET("/api/transaction/all-transactions/{userId}")
    Call<List<Transaction>> getAllTransactionsForUser(@Path("userId") int userId);

    @GET("/api/transaction/summary/{userId}")
    Call<TransactionSummary> getTransactionSummary(@Path("userId") int userId);

    @GET("/api/voucher")
    Call<List<Voucher>> getVouchers();

    @GET("/api/store")
    Call<List<Store>> getStores();

    @GET("/api/store/{id}")
    Call<Store> getStoreById(@Path("id") int storeId);

    // API lấy danh sách voucher theo storeId
    @GET("/api/voucher/store/{storeId}")
    Call<List<Voucher>> getVouchersByStoreId(@Path("storeId") int storeId);

    // API lấy danh sách product theo storeId
    @GET("/api/product/store/{storeId}")
    Call<List<Product>> getProductsByStoreId(@Path("storeId") int storeId);

    @POST("/api/user/loginUser")
    @FormUrlEncoded
    Call<TokenResponse> loginUser(
            @Field("emailOrPhone") String email,
            @Field("password") String password
    );

    @POST("/api/transaction/place-order-transaction")
    Call<ApiResponse<OrderTransactionResponse>> placeOrderWithTransaction(@Body OrderTransactionRequest request);

    @POST("/api/payos/topup")
    Call<TopUpResponse> topUp(@Body TopUpRequest request);


}
