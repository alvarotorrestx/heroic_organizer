package com.example.heroicorganizer.callback;

import com.example.heroicorganizer.model.LibraryFolder;

import java.util.List;

public interface LibraryFolderCallback {
    void onSuccess(String message);
    void onSuccessFolders(List<LibraryFolder> folders);
    void onFailure(String errorMessage);
}
