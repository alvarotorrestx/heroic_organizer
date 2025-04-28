package com.example.heroicorganizer.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.callback.LibraryFolderCallback;
import com.example.heroicorganizer.model.LibraryFolder;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.LibraryFolderPresenter;
import com.example.heroicorganizer.ui.ToastMsg;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class LibraryCreateFolderFragment extends Fragment {

    public LibraryCreateFolderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText folderId = view.findViewById(R.id.folderId);
        final EditText folderName = view.findViewById(R.id.folderName);
        final EditText folderDescription = view.findViewById(R.id.folderDescription);
        final EditText folderCoverImg = view.findViewById(R.id.folderCoverImg);
        final EditText folderColorTag = view.findViewById(R.id.folderColorTag);
        final Button newFolder = view.findViewById(R.id.newFolder);
        final Button updateFolder = view.findViewById(R.id.updateFolder);
        final Button deleteFolder = view.findViewById(R.id.deleteFolder);

        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());

        newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = folderId.getText().toString();
                String name = folderName.getText().toString();
                String description = folderDescription.getText().toString();
                String coverImg = folderCoverImg.getText().toString();
                String colorTag = folderColorTag.getText().toString();
                int totalComics = 0;

                LibraryFolder folder = new LibraryFolder(id, name, description, coverImg, colorTag, totalComics);

                LibraryFolderPresenter.createFolder(currentUser, folder, new LibraryFolderCallback() {
                    public void onSuccess(String message) {
                        ToastMsg.show(requireContext(), message);
                    }

                    public void onSuccessFolders(List<LibraryFolder> folders) {
                    }

                    public void onFailure(String message) {
                        ToastMsg.show(requireContext(), message);
                    }
                });
            }
        });

        updateFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = folderId.getText().toString();
                String name = folderName.getText().toString();
                String description = folderDescription.getText().toString();
                String coverImg = folderCoverImg.getText().toString();
                String colorTag = folderColorTag.getText().toString();
                int totalComics = 0; // TODO: Update this to pull in current total of comics in updating folder.

                LibraryFolder folder = new LibraryFolder(id, name, description, coverImg, colorTag, totalComics);

                if (totalComics == 0) { // TODO: reminder for the above todo
                    ToastMsg.show(requireContext(), "Al. Please update method.");
                    return;
                }

                LibraryFolderPresenter.updateFolder(currentUser, folder, new LibraryFolderCallback() {
                    public void onSuccess(String message) {
                        ToastMsg.show(requireContext(), message);
                    }

                    public void onSuccessFolders(List<LibraryFolder> folders) {
                    }

                    public void onFailure(String message) {
                        ToastMsg.show(requireContext(), message);
                    }
                });
            }
        });

        deleteFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = folderId.getText().toString();

                LibraryFolderPresenter.deleteFolder(currentUser, id, new LibraryFolderCallback() {
                    public void onSuccess(String message) {
                        ToastMsg.show(requireContext(), message);
                    }

                    public void onSuccessFolders(List<LibraryFolder> folders) {
                    }

                    public void onFailure(String message) {
                        ToastMsg.show(requireContext(), message);
                    }
                });
            }
        });

    }
}
