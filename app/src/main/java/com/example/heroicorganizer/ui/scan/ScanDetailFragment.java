package com.example.heroicorganizer.ui.scan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
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

public class ScanDetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView comicTitle = view.findViewById(R.id.coverComicName);
        ImageView comicImage = view.findViewById(R.id.capturedImageView);
        TextView comicIssue = view.findViewById(R.id.comicIssueNumber);
        TextView comicPublishDate = view.findViewById(R.id.coverDate);
        TextView comicPublishers = view.findViewById(R.id.comicPublishers);
        TextView comicVariantId = view.findViewById(R.id.coverVariant);
        TextView comicArtist = view.findViewById(R.id.coverArtistVariant);
        TextView comicAuthor = view.findViewById(R.id.coverComicAuthor);
        TextView comicUPC = view.findViewById(R.id.coverUPC);
        TextView comicDescription = view.findViewById(R.id.comicDescription);

        // Pull in bundle from ScanFragment
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title");
            String image = getArguments().getString("image");
            String issueNumber = getArguments().getString("issueNumber");
            String publisher_names = getArguments().getString("publisher_names");
            String cover_artist = getArguments().getString("cover_artist");
            String author = getArguments().getString("author");
            String datePublished = getArguments().getString("datePublished");
            String upc = getArguments().getString("upc");
            String description = getArguments().getString("description");
            String parentComicTitle = getArguments().getString("parentComicTitle");
            String parentComicId = getArguments().getString("parentComicId");
            String parentComicIssueNumber = getArguments().getString("parentComicIssueNumber");
            List<String> comicVariants = getArguments().getStringArrayList("comicVariants");

            // Set each view to the appropriate response
            comicTitle.setText(title);
            comicIssue.setText(issueNumber);
            comicPublishDate.setText(datePublished);
            comicPublishers.setText(publisher_names);
            comicAuthor.setText(author);
            comicArtist.setText(cover_artist);
            comicUPC.setText(upc);
            comicDescription.setText(description);

            // Show captured image
            if (image != null) {
                byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);
                Bitmap convertedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                comicImage.setImageBitmap(convertedImage);
            }

        }
    }
}
