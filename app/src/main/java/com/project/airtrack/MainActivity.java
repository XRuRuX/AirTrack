package com.project.airtrack;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity is the entry point of the app, handling the main user interface and navigation.
 * It initializes the activity, sets up edge-to-edge display, and manages fragment transactions
 * based on user interaction with the bottom navigation menu.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        replaceFragment(new HomeFragment());
        setupBottomNavigationMenu();
        setupStatusBar();
    }

    // Configures the bottom navigation menu and sets up item selection listener
    private void setupBottomNavigationMenu() {
        BottomNavigationView bottomNavigationMenu = findViewById(R.id.bottomNavigationMenu);

        bottomNavigationMenu.setItemActiveIndicatorColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black)));

        bottomNavigationMenu.setOnItemSelectedListener(item -> {
            // Replaces the current fragment based on the selected menu item
            if (item.getItemId() == R.id.navigation_home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.navigation_thermometer) {
                replaceFragment(new TemperatureFragment());
            } else if (item.getItemId() == R.id.navigation_air) {
                replaceFragment(new AirFragment());
            } else if (item.getItemId() == R.id.navigation_settings) {
                replaceFragment(new SettingsFragment());
            } else {
                return false;
            }
            return true;
        });
    }

    // Setup status bar colors
    private void setupStatusBar() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));        // Change Android Status Bar color
        window.getDecorView().setSystemUiVisibility(0);                                      // Change Android Status Bar and Navigation Bar icons to white
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));   // Change Android Navigation Bar color
    }

    // Replaces the current fragment with a new one if they are different
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fl_central_container);

        // Replace the fragment only if it's different from the current one
        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_central_container, fragment);
            fragmentTransaction.commit();
        }
    }
}