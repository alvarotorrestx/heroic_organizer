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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.callback.LibraryFolderCallback;
import com.example.heroicorganizer.model.LibraryFolder;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.LibraryFolderPresenter;
import com.example.heroicorganizer.ui.ToastMsg;
import com.example.heroicorganizer.utils.ViewStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.bumptech.glide.Glide;

import java.util.List;

public class LibraryFragment extends Fragment {

    public LibraryFragment() {
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
        inflater.inflate(R.menu.menu_library, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull android.view.Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem addFolderItem = menu.findItem(R.id.addFolder);
        if (addFolderItem != null) {
            // only shows addFolder when in LibraryFragment
            addFolderItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addFolder) {

            // navigate to sub-level fragment logic
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_library_create);

            return true;
        }

        //
        // Al Uncomment this when you are ready to wire the navigation to the button found in menu_library.xml
        //
//        else if (item.getItemId() == R.id.modifyFolder) {
//            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
//            // this ID should match your navigation graph
//            navController.navigate(R.id.nav_library_modify);
//        }
        return super.onOptionsItemSelected(item);
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

        // Shows loading message to user (ux)
        folderContainer.addView(ViewStatus.SetStatus(requireContext(), "Loading Folders..."));

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
                        View folderCard = inflater.inflate(R.layout.item_card, folderContainer, false);

                        ImageView coverImage = folderCard.findViewById(R.id.itemCoverImage);
                        TextView folderName = folderCard.findViewById(R.id.itemTitle);
                        TextView comicCount = folderCard.findViewById(R.id.itemSubtitle);

                        folderName.setText(folder.getName());

                        comicCount.setText(folder.getTotalComics() + " Comics");

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

                        // Pass folderId for getAllComicsInFolder method
                        folderCard.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("folderName", folder.getName());
                            bundle.putString("folderId", folder.getId());

                            // navigate to sub-level fragment logic
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                            navController.navigate(R.id.nav_library_comics, bundle);
                        });
                    }
                } else { // If no folders are created by user or found
                    folderContainer.addView(ViewStatus.SetStatus(requireContext(), "No folders found."));
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
