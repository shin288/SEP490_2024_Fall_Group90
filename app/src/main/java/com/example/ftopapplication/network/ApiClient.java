package com.example.ftopapplication.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static final String BASE_URL = "https://your-api-url.com"; // Thay bằng URL API backend thực tế
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Thêm HttpLoggingInterceptor để log request và response
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Tạo OkHttpClient
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor) // Gắn interceptor để log dữ liệu
                    .connectTimeout(30, TimeUnit.SECONDS) // Thời gian chờ kết nối
                    .readTimeout(30, TimeUnit.SECONDS)    // Thời gian chờ đọc
                    .writeTimeout(30, TimeUnit.SECONDS)   // Thời gian chờ ghi
                    .build();

            // Khởi tạo Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // URL gốc của API
                    .client(okHttpClient) // Gắn OkHttpClient vào Retrofit
                    .addConverterFactory(GsonConverterFactory.create()) // Thêm converter JSON
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
