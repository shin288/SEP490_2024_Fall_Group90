package com.example.ftopapplication;

import com.example.ftopapplication.data.model.User;

public class TokenResponse {
    private String access_token;
    private String refresh_token;


    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String refresh_token) {
        this.refresh_token = refresh_token;
    }


}
