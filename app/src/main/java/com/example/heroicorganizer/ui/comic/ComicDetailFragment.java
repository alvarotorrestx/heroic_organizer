package com.example.heroicorganizer.ui.comic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.ui.library.LibraryFragment;
import com.google.firebase.auth.FirebaseAuth;

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

        // Sends user back to the Search Result(s) page
        backToSearch.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               returnToSearch();
           }
        });
    }

    private void returnToSearch() {
        // Sends user back to the Search Result(s) page
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.search_fragment_container, new LibraryFragment())
                .commit();
    }
}
