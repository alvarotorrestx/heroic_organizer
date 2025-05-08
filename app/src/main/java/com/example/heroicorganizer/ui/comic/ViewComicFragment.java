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

import java.util.ArrayList;
import java.util.List;

public class ViewComicFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comic_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User currentUser = new User();
        currentUser.setUid(FirebaseAuth.getInstance().getUid());

        final ImageView comicCoverImage = view.findViewById(R.id.comicCoverImage);
        final TextView comicTitle = view.findViewById(R.id.comicTitle);
        final TextView comicDeck = view.findViewById(R.id.comicDeck);
        final TextView comicDescription = view.findViewById(R.id.comicDescription);
        final TextView comicPublishers = view.findViewById(R.id.comicPublishers);
        final TextView comicIssueNumber = view.findViewById(R.id.comicIssueNumber);

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
        }
    }
}
