package com.example.heroicorganizer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.presenter.LogoutPresenter;
import com.example.heroicorganizer.ui.login.Onboarding;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ///
        View activityLayout = findViewById(R.id.settings_container);
        activityLayout.setAlpha(0);
        activityLayout.animate().alpha(1).setDuration(1000);
        ///

        findViewById(R.id.btn_close_settings).setOnClickListener(v -> {
            ///
            activityLayout.animate().alpha(0).setDuration(1000).withEndAction(() -> {
            ///
                finish();
            });
        });

        findViewById(R.id.pref_app).setOnClickListener(v ->
                ToastMsg.show(this, "App Preferences"));

        findViewById(R.id.pref_notifications).setOnClickListener(v ->
                ToastMsg.show(this, "Notifications"));

        findViewById(R.id.pref_accessibility).setOnClickListener(v ->
                ToastMsg.show(this, "Accessibility"));

        findViewById(R.id.pref_help).setOnClickListener(v ->
                ToastMsg.show(this, "Help"));

        findViewById(R.id.pref_activity_log).setOnClickListener(v ->
                ToastMsg.show(this, "Activity Log"));

        findViewById(R.id.pref_account).setOnClickListener(v ->
                ToastMsg.show(this, "Account Settings"));

        findViewById(R.id.pref_logout).setOnClickListener(v -> {
            LogoutPresenter.logoutUser();
            ToastMsg.show(this, "Logging out...");
            ///
            activityLayout.animate().alpha(0).setDuration(1000).withEndAction(() -> {
            ///
            Intent intent = new Intent(this, Onboarding.class);
            startActivity(intent);
            finish();
            });
        });

    }
}
