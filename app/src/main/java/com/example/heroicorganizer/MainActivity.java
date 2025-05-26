package com.example.heroicorganizer;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import androidx.core.view.GravityCompat;
import com.example.heroicorganizer.ui.wishlist.WishlistData;
import com.example.heroicorganizer.ui.wishlist.WishlistItem;
import com.example.heroicorganizer.utils.WeaviateConfig;
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
                Snackbar.make(view, "Dun dun dun dun… *EATS YOUR ENTIRE TEAM* YUMMY!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show()
        );

        // Temporary always check if schema is created to ensure Schema is created
        WeaviateConfig config = new WeaviateConfig();
        config.checkWeaviateSchema();

        // Temporary create static list of comics for wishlist
        // TODO: Remove once Firebase DB for Wishlist is completed
        if (WishlistData.itemList.isEmpty()) {
            WishlistData.itemList.add(new WishlistItem(R.drawable.example4, "Marvel Rivals", "#1", "Peach Momoko", "Apr/2/2025", ""));
            WishlistData.itemList.add(new WishlistItem(R.drawable.example1, "Magik", "#4", "Rose Besch", "Apr/23/2025", "Mar/24/2025"));
            WishlistData.itemList.add(new WishlistItem(R.drawable.example3, "Jeff the Land Shark", "#1", "Todd Nauck Homage", "Jun/18/2025", "May/05/2026"));
            WishlistData.itemList.add(new WishlistItem(R.drawable.example2, "Psylocke", "#8", "Puppeteer Lee", "Jun/18/2025", "May/19/2025"));
        }

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

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // refreshes the menu when the destination changes
            invalidateOptionsMenu();
        });

        // drawer item click handling
        findViewById(R.id.menu_home).setOnClickListener(v -> {
            navController.navigate(R.id.nav_home_fade);
            drawer.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_library).setOnClickListener(v -> {
            navController.navigate(R.id.nav_library);
            drawer.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_wishlist).setOnClickListener(v -> {
            navController.navigate(R.id.nav_wishlist_fade);
            drawer.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_scan).setOnClickListener(v -> {
            navController.navigate(R.id.nav_scan_fade);
            drawer.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_locator).setOnClickListener(v -> {
            navController.navigate(R.id.nav_locator_fade);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_search_fade);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}