package com.project.airtrack;

import com.project.airtrack.application.AirTrackApplication;
import com.project.airtrack.bluetooth.BluetoothManager;
import com.project.airtrack.bluetooth.DataMediator;
import com.project.airtrack.bluetooth.Mediator;
import com.project.airtrack.bluetooth.OnDataReceivedListener;
import com.project.airtrack.data.database.ApplicationDatabase;
import com.project.airtrack.data.database.dao.SensorDataDAO;
import com.project.airtrack.data.database.entities.SensorsData;
import com.project.airtrack.data.processing.DataParser;
import com.project.airtrack.data.processing.DataProcessor;
import com.project.airtrack.data.processing.EnvironmentalData;
import com.project.airtrack.utils.TimeFormatter;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * HomeFragment handles the UI for the home screen of the app.
 */
public class HomeFragment extends Fragment implements OnDataReceivedListener {

    private BluetoothManager bluetoothManager;
    private TextView tvIndicatorValue;
    private TextView tvLastUpdated;
    private Mediator mediator;
    private DataProcessor processor;
    private int lastUpdatedTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = initializeLayout(inflater, container);
        processor = new DataParser();
        mediator = new DataMediator(processor, this);
        setupBluetoothManager();
        setupProgressBar(view);
        updateUIWithLastSensorData();

        // Setup scheduler to update the UI with the last
        ScheduledExecutorService lastTimeUpdatedScheduler = Executors.newScheduledThreadPool(1);
        lastTimeUpdatedScheduler.scheduleWithFixedDelay(this::refreshLastUpdatedTime, 0, 1, TimeUnit.MINUTES);

        return view;
    }

    // Initializes the layout for the fragment and retrieves references to UI components
    private View initializeLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvIndicatorValue = view.findViewById(R.id.tv_indicator_value);
        tvLastUpdated = view.findViewById(R.id.tv_last_updated);
        return view;
    }

    // Configures and initializes BluetoothManager. Connects to the device if Bluetooth is available
    private void setupBluetoothManager() {
        bluetoothManager = new BluetoothManager(requireContext(), mediator);

        if (!bluetoothManager.isBluetoothAvailable() && tvIndicatorValue != null) {
            //tvIndicatorValue.setText("ERR");
            return;
        }
        bluetoothManager.tryToConnectToDevice("AirTrack");
    }

    // Configures the progress bar UI element
    private void setupProgressBar(View view) {
        CircularSegmentedProgressBar progressBar = view.findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setProgress(3);
        }
    }

    // Refresh the UI with the last time that the device received data
    private void refreshLastUpdatedTime(){
        int currentTimestamp = (int) (System.currentTimeMillis() / 1000);
        int timePassedSinceLastUpdate = currentTimestamp - lastUpdatedTime;

        // Updating text views only works on the UI Thread
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                tvLastUpdated.setText("Last updated: " + TimeFormatter.secondsToStringFormat(timePassedSinceLastUpdate));
            });
        }
    }

    // Test database future implementation in management system
    private void updateUIWithLastSensorData() {
        // Test database
        ApplicationDatabase db = AirTrackApplication.getDatabase();
        SensorDataDAO sensorDataDAO = db.sensorDataDAO();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SensorsData sensorsData = sensorDataDAO.getLastSensorData();
                if(sensorsData != null)
                {
                    EnvironmentalData lastSensorValue = sensorsData.toEnvironmentalData();
                    onDataReceived(lastSensorValue);
                }
            }
        }).start();
    }

    @Override
    public void onDataReceived(EnvironmentalData data) {
        // Update the UI with data received via Bluetooth
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (tvIndicatorValue != null) {
                    tvIndicatorValue.setText(String.valueOf(data.getPm25()));
                }
                if(tvLastUpdated != null) {
                    int currentTimestamp = (int) (System.currentTimeMillis() / 1000);
                    lastUpdatedTime = data.getTimestamp();
                    int timePassedSinceLastUpdate = currentTimestamp - lastUpdatedTime;
                    tvLastUpdated.setText("Last updated: " + TimeFormatter.secondsToStringFormat(timePassedSinceLastUpdate));
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Disconnect BluetoothManager when the fragment is destroyed
        if (bluetoothManager != null) {
            bluetoothManager.disconnect();
        }
    }
}
