package com.example.ftopapplication;

import android.content.SharedPreferences;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    private SharedPreferences sharedPreferences;

    public AuthInterceptor(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String accessToken = sharedPreferences.getString("access_token", null);

        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        if (accessToken != null) {
            builder.header("Authorization", "Bearer " + accessToken);
        }

        Request request = builder.build();
        return chain.proceed(request);
    }
}

