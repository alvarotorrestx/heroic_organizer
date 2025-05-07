package com.example.heroicorganizer.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.heroicorganizer.R;

public class ModalBox {
    public static Dialog Show(Context context, String message, View.OnClickListener confirm, View.OnClickListener cancel) {
        // Custom modal box layout
        View modalView = LayoutInflater.from(context).inflate(R.layout.modal_box, null);

        // Sets message to show
        TextView messageView = modalView.findViewById(R.id.modalMessage);
        messageView.setText(message);

        // Button choices
        Button confirmButton = modalView.findViewById(R.id.confirmDelete);
        Button cancelButton = modalView.findViewById(R.id.cancelDelete);

        // Create the Dialog box
        AlertDialog modalBox = new AlertDialog.Builder(context)
                .setView(modalView)
                .setCancelable(true)
                .create();

        confirmButton.setOnClickListener(v -> {
            modalBox.dismiss();
            if (confirm != null) confirm.onClick(v);
        });

        cancelButton.setOnClickListener(v -> {
            modalBox.dismiss();
            if (cancel != null) cancel.onClick(v);
        });

        return modalBox;
    }
}
