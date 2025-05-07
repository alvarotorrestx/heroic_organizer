package com.example.heroicorganizer.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

public class ViewStatus {
   public static TextView SetStatus(Context context, String message) {
       // Init the TextView
       TextView messageText = new TextView(context);

       // Sets message and attributes
       messageText.setText(message);
       messageText.setTextColor(context.getResources().getColor(android.R.color.white));
       messageText.setTextSize(18);
       messageText.setGravity(Gravity.CENTER);

       // Returns the message
       return messageText;
   }
}
