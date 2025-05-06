package com.example.heroicorganizer.ui.library;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.callback.LibraryComicCallback;
import com.example.heroicorganizer.callback.LibraryFolderCallback;
import com.example.heroicorganizer.model.LibraryComic;
import com.example.heroicorganizer.model.LibraryFolder;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.LibraryComicPresenter;
import com.example.heroicorganizer.presenter.LibraryFolderPresenter;
import com.example.heroicorganizer.ui.ToastMsg;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class LibraryComicsFragment extends Fragment {

    public LibraryComicsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // allows fragment to handle menu changes
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Passed in Bundle from LibraryFragment - Comic Details
        Bundle passedBundle = getArguments() != null ? getArguments() : null;

        // Dynamically updates the page title to {Folder Name} - Comics on navbar
        String folderName = passedBundle.getString("folderName", "");
        if (!folderName.isEmpty()) ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(folderName + " - Comics");

        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());

        final GridLayout comicsContainer = view.findViewById(R.id.comicsContainer);

        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Shows loading message to user (ux)
        TextView loadingText = new TextView(requireContext());
        loadingText.setText("Loading Comics...");
        loadingText.setTextColor(getResources().getColor(android.R.color.white));
        loadingText.setTextSize(18);
        loadingText.setGravity(View.TEXT_ALIGNMENT_CENTER);

        comicsContainer.addView(loadingText);

        String folderId = passedBundle.getString("folderId");

        LibraryComicPresenter.getComicsInFolder(currentUser, folderId, new LibraryComicCallback() {

            @Override
            public void onSuccess(String message) {

            }

            @Override
            public void onSuccessComics(List<LibraryComic> comics) {
                // Removes Loading...
                comicsContainer.removeAllViews();

                if (!comics.isEmpty()) {
                    for (LibraryComic comic : comics) {
                        View comicCard = inflater.inflate(R.layout.folder_card, comicsContainer, false);

                        ImageView coverImage = comicCard.findViewById(R.id.folderCoverImage);
                        TextView folderName = comicCard.findViewById(R.id.folderName);
                        TextView comicCount = comicCard.findViewById(R.id.folderComicCount);

                        folderName.setText(comic.getTitle());
                        comicCount.setText(comic.getDeck());

                        if (comic.getCoverImage() != null && !comic.getCoverImage().isEmpty()) {
                            Glide.with(requireContext())
                                    .load(comic.getCoverImage())
                                    .into(coverImage);
                        } else {
                            coverImage.setVisibility(View.GONE);
                        }

                        comicsContainer.addView(comicCard);
                    }
                } else { // If no folders are created by user or found
                    TextView noComics = new TextView(requireContext());
                    noComics.setText("No comics found.");
                    noComics.setTextColor(getResources().getColor(android.R.color.white));
                    noComics.setTextSize(18);
                    noComics.setGravity(View.TEXT_ALIGNMENT_CENTER);

                    comicsContainer.addView(noComics);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Removes Loading...
                comicsContainer.removeAllViews();

                ToastMsg.show(requireContext(), errorMessage);
            }
        });

    }
}
