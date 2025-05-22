package com.example.heroicorganizer.callback;

import com.example.heroicorganizer.model.WeaviateSearchResult;

import java.util.List;

public interface WeaviateSearchImageCallback {
    void onSuccess(WeaviateSearchResult searchResult);
    void onFailure(String errorMessage);
}
