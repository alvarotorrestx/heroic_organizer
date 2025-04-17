package com.example.heroicorganizer.callback;

public interface RegisterCallback {
    void onSuccess(String username);
    void onFailure(String errorMessage);
}
