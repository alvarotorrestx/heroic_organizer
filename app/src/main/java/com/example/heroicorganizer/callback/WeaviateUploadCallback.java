package com.example.heroicorganizer.callback;

public interface WeaviateUploadCallback {
    void onSuccess(String message);
    void onFailure(String errorMessage);
}
