package com.example.heroicorganizer.callback;

import com.example.heroicorganizer.model.MetronComic;

import java.util.List;

public interface MetronComicCallback {
    void onSuccess(List<MetronComic> results);
    void onFailure(String errorMessage);
}
