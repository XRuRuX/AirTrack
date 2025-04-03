package com.project.airtrack;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.airtrack.application.AirTrackApplication;
import com.project.airtrack.bluetooth.OnDataReceivedListener;
import com.project.airtrack.data.database.ApplicationDatabase;
import com.project.airtrack.data.database.dao.SensorDataDAO;
import com.project.airtrack.data.database.entities.SensorsData;
import com.project.airtrack.data.processing.EnvironmentalData;
import com.project.airtrack.utils.AQIFormatter;
import com.project.airtrack.utils.TimeFormatter;
import com.project.airtrack.visuals.chart.ChartManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * AirFragment is designed to display detailed information about air quality.
 * It manages and organizes various chart instances, displaying data from databases and real-time streams.
 */
public class AirFragment extends Fragment implements OnDataReceivedListener {
    private TextView tvLiveAQI;
    private TextView tvLivePm25;
    private TextView tvLivePm10;
    private TextView tvLiveOzone;
    private TextView tvLastUpdated;
    ChartManager chartManager;
    private int lastUpdatedTime;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = initializeLayout(inflater, container);
        updateUIWithLastSensorData();

        // Setup the scheduler to update the UI with the last time it received data
        ScheduledExecutorService lastTimeUpdatedScheduler = Executors.newScheduledThreadPool(1);
        lastTimeUpdatedScheduler.scheduleWithFixedDelay(this::refreshLastUpdatedTime, 1, 1, TimeUnit.MINUTES);

        chartManager = new ChartManager(view, requireContext());
        chartManager.loadDataFromDatabase(requireActivity());

        return view;
    }

    // Initializes the layout for the fragment and retrieves references to UI components
    private View initializeLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_air, container, false);
        tvLiveAQI = view.findViewById(R.id.tv_live_aqi);
        tvLivePm25 = view.findViewById(R.id.tv_live_pm25);
        tvLivePm10 = view.findViewById(R.id.tv_live_pm10);
        tvLiveOzone = view.findViewById(R.id.tv_live_ozone);
        tvLastUpdated = view.findViewById(R.id.tv_last_updated);

        return view;
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
                }
            }
        }).start();
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

    @Override
    public void onDataReceived(EnvironmentalData data) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (tvLiveAQI != null) {
                    tvLiveAQI.setText("AQI: " + data.getMaximumAQI());
                }
                if(tvLivePm25 != null) {
                    tvLivePm25.setText("PM2.5 AQI: " + data.getPm25AQI());
                }
                if(tvLivePm10 != null) {
                    tvLivePm10.setText("PM10 AQI: " + data.getPm10AQI());;
                }
                if(tvLiveOzone != null) {
                    tvLiveOzone.setText("Ozone AQI: " + data.getOzoneAQI());
                }
                if(tvLastUpdated != null) {
                    int currentTimestamp = (int) (System.currentTimeMillis() / 1000);
                    lastUpdatedTime = data.getTimestamp();
                    int timePassedSinceLastUpdate = currentTimestamp - lastUpdatedTime;
                    tvLastUpdated.setText("Last updated: " + TimeFormatter.secondsToStringFormat(timePassedSinceLastUpdate));
                }
                // Stream live data to ChartManager for chart updates
                chartManager.onLiveDataReceived(data);
            });
        }
    }
}