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
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.callback.LibraryFolderCallback;
import com.example.heroicorganizer.model.LibraryFolder;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.LibraryFolderPresenter;
import com.example.heroicorganizer.ui.ToastMsg;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class LibraryModifyFolderFragment extends Fragment {

    public LibraryModifyFolderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library_modify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText folderName = view.findViewById(R.id.folderName);
        final EditText folderDescription = view.findViewById(R.id.folderDescription);
        final EditText folderCoverImg = view.findViewById(R.id.folderCoverImg);
        final EditText folderColorTag = view.findViewById(R.id.folderColorTag);
        final Button updateFolder = view.findViewById(R.id.updateFolder);

        // Set fields to the passed in data from LibraryComicsFragment
        Bundle passedBundle = getArguments() != null ? getArguments() : null;
        String folderId = passedBundle.getString("folderId");
        folderName.setText(passedBundle.getString("folderName"));
        folderDescription.setText(passedBundle.getString("folderDescription"));
        folderCoverImg.setText(passedBundle.getString("folderCoverImg"));
        folderColorTag.setText(passedBundle.getString("folderColorTag"));
        int totalComics = passedBundle.getInt("totalComics");

        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());

        updateFolder.setOnClickListener(v -> {
            LibraryFolder updatedFolder = new LibraryFolder();
            updatedFolder.setId(folderId);
            updatedFolder.setName(folderName.getText().toString());
            updatedFolder.setDescription(folderDescription.getText().toString());
            updatedFolder.setCoverImage(folderCoverImg.getText().toString());
            updatedFolder.setColorTag(folderColorTag.getText().toString());
            updatedFolder.setTotalComics(totalComics);

            LibraryFolderPresenter.updateFolder(currentUser, updatedFolder, new LibraryFolderCallback() {

                @Override
                public void onSuccess(String message) {
                    ToastMsg.show(getActivity(), message);

                    Bundle updatedFolderBundle = new Bundle();
                    updatedFolderBundle.putString("folderId", updatedFolder.getId());
                    updatedFolderBundle.putString("folderName", updatedFolder.getName());

                    // Navigate to previous folder - the now updated folder
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

                    // Prevents a back stack to the update folder page with previous data
                    NavOptions navOptions = new NavOptions.Builder()
                            .setPopUpTo(R.id.nav_library_comics, true)
                            .build();

                    navController.navigate(R.id.nav_library_comics, updatedFolderBundle, navOptions);
                }

                @Override
                public void onSuccessFolders(List<LibraryFolder> folders) {

                }

                @Override
                public void onFailure(String errorMessage) {
                    ToastMsg.show(getActivity(), errorMessage);
                }
            });
        });
    }
}
