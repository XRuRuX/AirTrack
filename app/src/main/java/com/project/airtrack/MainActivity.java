package com.project.airtrack;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.project.airtrack.bluetooth.BluetoothManager;
import com.project.airtrack.bluetooth.DataMediator;
import com.project.airtrack.bluetooth.Mediator;
import com.project.airtrack.bluetooth.OnDataReceivedListener;
import com.project.airtrack.data.processing.DataParser;
import com.project.airtrack.data.processing.DataProcessor;

/**
 * MainActivity is the entry point of the app, handling the main user interface and navigation.
 * It initializes the activity, sets up edge-to-edge display, and manages fragment transactions
 * based on user interaction with the bottom navigation menu.
 */
public class MainActivity extends AppCompatActivity {
    private Fragment homeFragment;
    private Fragment temperatureFragment;
    private Fragment airFragment;
    private Fragment settingsFragment;
    private Fragment activeFragment;
    private Mediator mediator;
    private DataProcessor processor;
    private BluetoothManager bluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initializeFragments();
        setupBottomNavigationMenu();
        setupStatusBar();
    }

    // Initialize fragments
    private void initializeFragments() {
        homeFragment = new HomeFragment();
        temperatureFragment = new TemperatureFragment();
        airFragment = new AirFragment();
        settingsFragment = new SettingsFragment();

        activeFragment = homeFragment;

        // Add fragments to the container and hide the ones that are not active
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_central_container, settingsFragment, "settings").hide(settingsFragment)
                .add(R.id.fl_central_container, airFragment, "air").hide(airFragment)
                .add(R.id.fl_central_container, temperatureFragment, "temperature").hide(temperatureFragment)
                .add(R.id.fl_central_container, homeFragment, "home")
                .commit();

        processor = new DataParser();
        mediator = new DataMediator(processor, (OnDataReceivedListener) homeFragment, (OnDataReceivedListener) temperatureFragment);
        setupBluetoothManager();
    }

    // Configures and initializes BluetoothManager. Connects to the device if Bluetooth is available
    private void setupBluetoothManager() {
        bluetoothManager = new BluetoothManager(this, mediator);
        bluetoothManager.tryToConnectToDevice("AirTrack");
    }

    // Configures the bottom navigation menu and sets up item selection listener
    private void setupBottomNavigationMenu() {
        BottomNavigationView bottomNavigationMenu = findViewById(R.id.bottomNavigationMenu);

        bottomNavigationMenu.setItemActiveIndicatorColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black)));

        bottomNavigationMenu.setOnItemSelectedListener(item -> {
            // Replaces the current fragment based on the selected menu item
            if (item.getItemId() == R.id.navigation_home) {
                switchFragment(homeFragment);
            } else if (item.getItemId() == R.id.navigation_thermometer) {
                switchFragment(temperatureFragment);
            } else if (item.getItemId() == R.id.navigation_air) {
                switchFragment(airFragment);
            } else if (item.getItemId() == R.id.navigation_settings) {
                switchFragment(settingsFragment);
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

    // Toggles between displayed fragments, hiding the active fragment and showing the selected fragment
    private void switchFragment(Fragment selectedFragment) {
        if (selectedFragment != null && selectedFragment != activeFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(activeFragment);
            transaction.show(selectedFragment);
            transaction.commit();
            activeFragment = selectedFragment;
        }
    }
}