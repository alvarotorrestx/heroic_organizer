package com.example.heroicorganizer.ui.library;

import android.animation.TimeInterpolator;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
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
import com.example.heroicorganizer.utils.ModalBox;
import com.example.heroicorganizer.utils.ViewStatus;
import com.google.firebase.auth.FirebaseAuth;
import kotlinx.coroutines.Delay;

import java.util.List;

public class LibraryComicsFragment extends Fragment {

    public LibraryComicsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // allows fragment to handle menu changes
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // clears the toolbar main.xml 'menu'
        menu.clear();
        inflater.inflate(R.menu.menu_folder, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.updateFolder) {
            // Local scoped passedBundle - Collects passed data from LibraryFragment of current folder
            Bundle passedBundle = getArguments() != null ? getArguments() : null;
            String folderId = passedBundle.getString("folderId");
            String folderName = passedBundle.getString("folderName");
            String folderDescription = passedBundle.getString("folderDescription");
            String folderImage = passedBundle.getString("folderImage");
            String folderColor = passedBundle.getString("folderColor");
            int totalComics = passedBundle.getInt("totalComics");

            // Pass the bundle to the LibraryModifyFolderFragment for updating
            Bundle modifyBundle = new Bundle();
            modifyBundle.putString("folderId", folderId);
            modifyBundle.putString("folderName", folderName);
            modifyBundle.putString("folderDescription", folderDescription);
            modifyBundle.putString("folderCoverImg", folderImage);
            modifyBundle.putString("folderColorTag", folderColor);
            modifyBundle.putInt("totalComics", totalComics);

            // navigate to sub-level fragment logic
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_library_modify, modifyBundle);

            return true;
        } else if (item.getItemId() == R.id.deleteFolder) {

            // Local scoped passedBundle - Collects passed data from LibraryFragment of current folder
            Bundle passedBundle = getArguments() != null ? getArguments() : null;
            String folderId = passedBundle.getString("folderId");

            User currentUser = new User();
            currentUser.setUid(FirebaseAuth.getInstance().getUid());

            // Show a modal box to confirm user wants to delete current folder
            ModalBox.Show(getContext(), "Are you sure you want to delete this folder?",
                            v -> { // Confirm - Delete the folder

                                LibraryFolderPresenter.deleteFolder(currentUser, folderId, new LibraryFolderCallback() {
                                    @Override
                                    public void onSuccess(String message) {
                                        ToastMsg.show(getContext(), message);

                                        // Navigate to folders page
                                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                                        // Prevents a back stack to the deleted folder
                                        NavOptions navOptions = new NavOptions.Builder()
                                                .setPopUpTo(R.id.nav_library, true)
                                                .build();
                                        navController.navigate(R.id.nav_library, null, navOptions);
                                    }

                                    @Override
                                    public void onSuccessFolders(List<LibraryFolder> folders) {

                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        ToastMsg.show(getContext(), errorMessage);
                                    }
                                });

                            }, v -> { // Cancel
                            })
                    .show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Local scoped passedBundle - Collects passed data from LibraryFragment of current folder
        Bundle passedBundle = getArguments() != null ? getArguments() : null;

        // Dynamically updates the page title to {Folder Name} - Comics on navbar
        String folderName = passedBundle.getString("folderName", "");
        if (!folderName.isEmpty())
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(folderName + " - Comics");

        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());

        final GridLayout comicsContainer = view.findViewById(R.id.comicsContainer);

        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Shows loading message to user (ux)
        comicsContainer.addView(ViewStatus.SetStatus(requireContext(), "Loading Comics..."));

        // To pass into the getComicsInFolder method
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
                        View comicCard = inflater.inflate(R.layout.item_card, comicsContainer, false);

                        ImageView coverImage = comicCard.findViewById(R.id.itemCoverImage);
                        TextView comicTitle = comicCard.findViewById(R.id.itemTitle);
                        TextView comicSubtitle = comicCard.findViewById(R.id.itemSubtitle);

                        comicTitle.setText(comic.getTitle());
                        comicSubtitle.setText(comic.getDeck());

                        if (comic.getCoverImage() != null && !comic.getCoverImage().isEmpty()) {
                            Glide.with(requireContext())
                                    .load(comic.getCoverImage())
                                    .fitCenter()
                                    .into(coverImage);
                        } else {
                            coverImage.setVisibility(View.GONE);
                        }

                        // Bundle responses to push to the ViewComicFragment
                        comicCard.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("id", comic.getId() != null && !comic.getId().isEmpty() ? comic.getId() : "");
                            bundle.putString("title", comic.getTitle() != null && !comic.getTitle().isEmpty() ? comic.getTitle() : "");
                            bundle.putString("deck", comic.getDeck() != null && !comic.getDeck().isEmpty() ? comic.getDeck() : "");
                            bundle.putString("description", comic.getDescription() != null && !comic.getDescription().isEmpty() ? comic.getDescription() : "");
                            bundle.putString("image", comic.getCoverImage() != null && !comic.getCoverImage().isEmpty() ? comic.getCoverImage() : "");
                            bundle.putString("publishers", comic.getPublisher() != null && !comic.getPublisher().isEmpty() ? comic.getPublisher() : "");
                            bundle.putString("issueNumber", comic.getIssue() != null && !comic.getIssue().isEmpty() ? comic.getIssue() : "");

                            ///  Testing Animation methods
                            final ImageView comicCoverImage = view.findViewById(R.id.comicCoverImage);
//                            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(comicCoverImage.getWidth(), comicCoverImage.getHeight());
//                            lp.setMargins(0, 0, 0, 0);
//                            comicCard.setLayoutParams(lp);
                            comicCard.animate().setDuration(2500).translationY(250).scaleX(4).scaleY(4).alpha(0)
                                    .withEndAction(() -> {
//                                        final ImageView comicCoverImage = view.findViewById(R.id.comicCoverImage);
//                                        comicCard.clearAnimation();
//                                        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(comicCoverImage.getWidth(), comicCoverImage.getHeight());
//                                        lp.setMargins(50, 100, 0, 0);
//                                        comicCard.setLayoutParams(lp);


                                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                                            navController.navigate(R.id.fade_in_comic_view, bundle);

                                    });



                            ///

                            // navigate to sub-level fragment logic (commented out wh

//                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
//                            navController.navigate(R.id.nav_library_comic_view, bundle);
                        });

                        comicsContainer.addView(comicCard);
                    }
                } else { // If no comics are in the folder
                    comicsContainer.addView(ViewStatus.SetStatus(requireContext(), "No comics found."));
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
