package com.example.heroicorganizer.ui.comic;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.example.heroicorganizer.ui.library.LibraryFragment;
import com.example.heroicorganizer.ui.search.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ComicDetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comic_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());

        final Button backToSearch = view.findViewById(R.id.backToSearch);
        final ImageView comicCoverImage = view.findViewById(R.id.comicCoverImage);
        final EditText comicTitle = view.findViewById(R.id.comicTitle);
        final EditText comicDeck = view.findViewById(R.id.comicDeck);
        final EditText comicDescription = view.findViewById(R.id.comicDescription);
        final EditText comicPublishers = view.findViewById(R.id.comicPublishers);
        final EditText comicIssueNumber = view.findViewById(R.id.comicIssueNumber);
        final Spinner folderSpinner = view.findViewById(R.id.folderSpinner);
        final Button addToLibrary = view.findViewById(R.id.addToLibrary);

        // Sends user back to the Search Result(s) page
        // TODO: See if we can figure out how to return the same results (maybe cache)
        backToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToSearch();
            }
        });

        // Read from passed bundled responses from SearchFragment
        Bundle passedBundle = getArguments();
        if (passedBundle != null) {
            comicTitle.setText(passedBundle.getString("title"));
            comicDeck.setText(passedBundle.getString("deck"));
            // Cleans up HTML passed in from JSON response
            String rawHtml = passedBundle.getString("description");
            String cleanedDescription = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    ? Html.fromHtml(rawHtml, Html.FROM_HTML_MODE_LEGACY).toString()
                    : Html.fromHtml(rawHtml).toString();
            comicDescription.setText(cleanedDescription);
            comicPublishers.setText(passedBundle.getString("publishers"));
            comicIssueNumber.setText(passedBundle.getString("issueNumber"));

            Glide.with(requireContext())
                    .load(passedBundle.getString("image"))
                    .into(comicCoverImage);

            // For addToFolder method
            List<LibraryFolder> folderList = new ArrayList<>();

            // Shows available folders to add comic to library
            LibraryFolderPresenter.getFolders(currentUser, new LibraryFolderCallback() {
                @Override
                public void onSuccess(String message) {

                }

                @Override
                public void onSuccessFolders(List<LibraryFolder> folders) { // STOPPED HERE. Converting folderid to pass into method
                    List<String> folderNames = new ArrayList<>();
                    for (LibraryFolder folder : folders) {
                        folderList.add(folder);
                        folderNames.add(folder.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            folderNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    folderSpinner.setAdapter(adapter);
                }

                @Override
                public void onFailure(String errorMessage) {
                    ToastMsg.show(requireContext(), "Failed to load folders");
                    Log.e("FolderSpinner", "Error: " + errorMessage);
                }
            });

            // Add comic to selected folder
            addToLibrary.setOnClickListener(v -> {
                int selectedFolder = folderSpinner.getSelectedItemPosition();
                if (selectedFolder == Spinner.INVALID_POSITION || folderList.isEmpty()) {
                    ToastMsg.show(requireContext(), "Please select a folder");
                    return;
                }

                // Folder id to pass into method
                String folderId = folderList.get(selectedFolder).getId();

                // Variables to create a comic
                String comicId = passedBundle.getString("comicId");
                String title = comicTitle.getText().toString();
                String description = comicDescription.getText().toString();
                String deck = comicDeck.getText().toString();
                String publisher = comicPublishers.getText().toString();
                String issueNumber = comicIssueNumber.getText().toString();
                String coverImage = passedBundle.getString("image");

                LibraryComic comic = new LibraryComic(
                        comicId,
                        title,
                        description,
                        deck,
                        publisher,
                        issueNumber,
                        coverImage
                );

                LibraryComicPresenter.addComicToFolder(currentUser, folderId, comic, new LibraryComicCallback() {
                    @Override
                    public void onSuccess(String message) {
                        ToastMsg.show(requireContext(), "Comic successfully added to the folder!");
                    }

                    @Override
                    public void onSuccessComics(List<LibraryComic> comics) {

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        ToastMsg.show(requireContext(), errorMessage);
                    }
                });
            });
        }
    }

    private void returnToSearch() {
        // Sends user back to the Search Result(s) page
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.search_fragment_container, new SearchFragment())
                .commit();
    }
}
