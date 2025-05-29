package com.example.heroicorganizer.callback;

import java.util.List;

public interface ComicVineTeamsCallback {
    void onSuccess(List<String> teamNames);
    void onFailure(String errorMessage);
}
