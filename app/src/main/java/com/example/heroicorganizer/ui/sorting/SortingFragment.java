package com.example.heroicorganizer.ui.sorting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import com.example.heroicorganizer.R;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SortingFragment extends DialogFragment {

    public interface SortingCallback {
        void onSortSelected(String sortOption);
    }

    private SortingCallback callback;

    public void setCallback(SortingCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_sorting, null);

        view.findViewById(R.id.btn_sort_title).setOnClickListener(v -> sendResult("title"));
        view.findViewById(R.id.btn_sort_author).setOnClickListener(v -> sendResult("author"));
        view.findViewById(R.id.btn_sort_cover).setOnClickListener(v -> sendResult("cover"));
        view.findViewById(R.id.btn_sort_date).setOnClickListener(v -> sendResult("date"));

        builder.setView(view);
        return builder.create();
    }

    private void sendResult(String sortOption) {
        if (callback != null) {
            callback.onSortSelected(sortOption);
        }
        dismiss();
    }
}
