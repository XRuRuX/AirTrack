package com.project.airtrack;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        replaceFragment(new HomeFragment());
        setupBottomNavigationMenu();
    }

    // Configures the bottom navigation menu and its item selection listener
    private void setupBottomNavigationMenu() {
        BottomNavigationView bottomNavigationMenu = findViewById(R.id.bottomNavigationMenu);

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

    // Replaces the current fragment with the new one if they are different
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);

        // Check if the current fragment is different from the new fragment
        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        }
    }
}