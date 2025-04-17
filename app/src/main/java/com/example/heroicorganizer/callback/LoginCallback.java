package com.example.heroicorganizer.callback;

public interface LoginCallback {
    void onSuccess(String email);
    void onFailure(String errorMessage);
}
