package com.example.heroicorganizer.callback;

public interface WeaviateSearchImageCallback {
    void onSuccess(String title, String image);
    void onFailure(String errorMessage);
}
