package com.example.heroicorganizer.ui;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.heroicorganizer.R;

public class ToastMsg {
    public static void show(Context context, String message) {
        // custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View toastView = inflater.inflate(R.layout.toast_text, null);

        // message
        TextView messageView = toastView.findViewById(R.id.toast_message);
        messageView.setText(message);

        // toast
        Toast toast = new Toast(context);
        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 350);
        toast.show();
    }
}