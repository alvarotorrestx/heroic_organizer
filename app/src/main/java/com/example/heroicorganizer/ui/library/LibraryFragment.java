package com.example.heroicorganizer.ui.library;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;

import java.util.List;

public class LibraryFragment extends Fragment {

    public LibraryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());

        final GridLayout folderContainer = view.findViewById(R.id.folderContainer);

        LayoutInflater inflater = LayoutInflater.from(getContext());

        TextView loadingText = new TextView(requireContext());
        loadingText.setText("Loading...");
        loadingText.setTextColor(getResources().getColor(android.R.color.white));
        loadingText.setTextSize(18);
        loadingText.setGravity(View.TEXT_ALIGNMENT_CENTER);

        folderContainer.addView(loadingText);

        // Pull in all of user's folders
        LibraryFolderPresenter.getFolders(currentUser, new LibraryFolderCallback() {

            @Override
            public void onSuccess(String message) {
            }

            @Override
            public void onSuccessFolders(List<LibraryFolder> folders) {
                // Removes Loading...
                folderContainer.removeAllViews();

                if (!folders.isEmpty()) {
                    for (LibraryFolder folder : folders) {
                        View folderCard = inflater.inflate(R.layout.folder_card, folderContainer, false);

                        ImageView coverImage = folderCard.findViewById(R.id.folderCoverImage);
                        TextView folderName = folderCard.findViewById(R.id.folderName);
                        TextView comicCount = folderCard.findViewById(R.id.folderComicCount);

                        folderName.setText(folder.getName());

                        // TODO: Add field to count total comics in Folder
                        comicCount.setText("0 Comics");

                        if (folder.getCoverImage() != null && !folder.getCoverImage().isEmpty()) {
                            // Image package - rendering images with built-in caching
                            Glide.with(requireContext())
                                    .load(folder.getCoverImage())
                                    .into(coverImage);
                        } else if (folder.getColorTag() != null && !folder.getColorTag().isEmpty()) {
                            try {
                                int color = android.graphics.Color.parseColor(folder.getColorTag());
                                coverImage.setBackgroundColor(color);
                            } catch (IllegalArgumentException e) {
                                Log.e("LibraryFragment", "Invalid color format: " + folder.getColorTag());
                                coverImage.setBackgroundColor(Color.parseColor("#FFBB86FC"));
                            }
                        } else {
                            coverImage.setBackgroundColor(Color.parseColor("#FFBB86FC"));
                        }

                        folderContainer.addView(folderCard);
                    }
                } else { // If no folders are created by user or found
                    TextView noFolders = new TextView(requireContext());
                    noFolders.setText("No folders found.");
                    noFolders.setTextColor(getResources().getColor(android.R.color.white));
                    noFolders.setTextSize(18);
                    noFolders.setGravity(View.TEXT_ALIGNMENT_CENTER);

                    folderContainer.addView(noFolders);
                    return;
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Removes Loading...
                folderContainer.removeAllViews();

                ToastMsg.show(requireContext(), errorMessage);
            }
        });
    }
}
