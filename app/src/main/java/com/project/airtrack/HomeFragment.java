package com.project.airtrack;

import com.project.airtrack.application.AirTrackApplication;
import com.project.airtrack.bluetooth.OnDataReceivedListener;
import com.project.airtrack.data.database.ApplicationDatabase;
import com.project.airtrack.data.database.dao.SensorDataDAO;
import com.project.airtrack.data.database.entities.SensorsData;
import com.project.airtrack.data.processing.EnvironmentalData;
import com.project.airtrack.utils.AQIFormatter;
import com.project.airtrack.utils.TimeFormatter;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * HomeFragment handles the UI for the home screen of the app.
 */
public class HomeFragment extends Fragment implements OnDataReceivedListener {

    private TextView tvIndicatorValue;
    private TextView tvLastUpdated;
    private TextView tvIndicatorText;
    private TextView tvPollutant;
    private int lastUpdatedTime;
    CircularSegmentedProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = initializeLayout(inflater, container);
        progressBar = view.findViewById(R.id.progressBar);
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
        tvIndicatorText = view.findViewById(R.id.tv_indicator_text);
        tvPollutant = view.findViewById(R.id.tv_pollutant);
        return view;
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
                    EnvironmentalData lastAQIValue = sensorsData.toEnvironmentalData();
                    onDataReceived(lastAQIValue);
                    int maximumAQI = lastAQIValue.getMaximumAQI();
                    progressBar.setProgress(AQIFormatter.toProgress(maximumAQI));
                    tvIndicatorText.setText(AQIFormatter.toString(maximumAQI));
                    String pollutant = AQIFormatter.getPollutantWithHighestAQI(lastAQIValue);
                    tvPollutant.setText(pollutant);
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
                    int maximumAQI = data.getMaximumAQI();
                    tvIndicatorValue.setText(String.valueOf(maximumAQI));
                    progressBar.setProgress(AQIFormatter.toProgress(maximumAQI));
                    tvIndicatorText.setText(AQIFormatter.toString(maximumAQI));
                    String pollutant = AQIFormatter.getPollutantWithHighestAQI(data);
                    tvPollutant.setText(pollutant);
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
    }
}
