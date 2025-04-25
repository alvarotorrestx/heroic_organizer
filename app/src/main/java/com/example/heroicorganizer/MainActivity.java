package com.example.heroicorganizer;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import androidx.core.view.GravityCompat;
import com.google.android.material.snackbar.Snackbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.heroicorganizer.databinding.ActivityMainBinding;
import android.content.Intent;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        binding.appBarMain.fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show()
        );

        DrawerLayout drawer = binding.drawerLayout;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_library,
                R.id.nav_wishlist,
                R.id.nav_scan,
                R.id.nav_locator
        ).setOpenableLayout(drawer).build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        // drawer item click handling
        findViewById(R.id.menu_home).setOnClickListener(v -> {
            navController.navigate(R.id.nav_home);
            drawer.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_library).setOnClickListener(v -> {
            navController.navigate(R.id.nav_library);
            drawer.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_wishlist).setOnClickListener(v -> {
            navController.navigate(R.id.nav_wishlist);
            drawer.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_scan).setOnClickListener(v -> {
            navController.navigate(R.id.nav_scan);
            drawer.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_locator).setOnClickListener(v -> {
            navController.navigate(R.id.nav_locator);
            drawer.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_settings).setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.heroicorganizer.ui.SettingsActivity.class);
            startActivity(intent);
            drawer.closeDrawer(GravityCompat.START);
        });

        View closeBtn = findViewById(R.id.btn_close_drawer);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(v -> drawer.closeDrawer(GravityCompat.START));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}