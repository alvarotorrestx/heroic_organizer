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
import java.util.UUID;

public class LibraryCreateFolderFragment extends Fragment {

    public LibraryCreateFolderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText folderName = view.findViewById(R.id.folderName);
        final EditText folderDescription = view.findViewById(R.id.folderDescription);
        final EditText folderCoverImg = view.findViewById(R.id.folderCoverImg);
        final EditText folderColorTag = view.findViewById(R.id.folderColorTag);
        final Button newFolder = view.findViewById(R.id.newFolder);
        final Button backToFolders = view.findViewById(R.id.backToFolders);

        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());

        newFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = UUID.randomUUID().toString();
                String name = folderName.getText().toString();
                String description = folderDescription.getText().toString();
                String coverImg = folderCoverImg.getText().toString();
                String colorTag = folderColorTag.getText().toString();
                int totalComics = 0;

                LibraryFolder folder = new LibraryFolder(id, name, description, coverImg, colorTag, totalComics);

                LibraryFolderPresenter.createFolder(currentUser, folder, new LibraryFolderCallback() {
                    public void onSuccess(String message) {
                        ToastMsg.show(requireContext(), message);

                        // Sends user back to the Library Folder(s) page
                        returnToFolders();
                    }

                    public void onSuccessFolders(List<LibraryFolder> folders) {
                    }

                    public void onFailure(String message) {
                        ToastMsg.show(requireContext(), message);
                    }
                });
            }
        });

        backToFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToFolders();
            }
        });
    }



    private void returnToFolders() {
        // Sends user back to the Library Folder(s) page
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.library_fragment_container, new LibraryFragment())
                .commit();
    }
}
