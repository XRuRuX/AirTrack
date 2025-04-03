package com.project.airtrack;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
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
import com.project.airtrack.utils.TimeFormatter;

import android.text.SpannableString;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TemperatureFragment is responsible for displaying detailed information about
 * temperature and humidity
 * Additional functionality for showing temperature and humidity data will be added in the future.
 */
public class TemperatureFragment extends Fragment implements OnDataReceivedListener {
    private TextView tvTemperatureValue;
    private TextView tvHumidityValue;
    private TextView tvLastUpdated;
    private int lastUpdatedTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = initializeLayout(inflater, container);
        updateUIWithLastSensorData();

        // Setup the scheduler to update the UI with the last time it received data
        ScheduledExecutorService lastTimeUpdatedScheduler = Executors.newScheduledThreadPool(1);
        lastTimeUpdatedScheduler.scheduleWithFixedDelay(this::refreshLastUpdatedTime, 1, 1, TimeUnit.MINUTES);

        return view;
    }

    // Initializes the layout for the fragment and retrieves references to UI components
    private View initializeLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);
        tvTemperatureValue = view.findViewById(R.id.tv_temperature_value);
        tvHumidityValue = view.findViewById(R.id.tv_humidity_value);
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

    @Override
    public void onDataReceived(EnvironmentalData data) {
        // Update the UI with data received via Bluetooth
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (tvTemperatureValue != null) {
                    String temperature = String.valueOf(data.getTemperature());
                    String fullText = temperature + "°C";

                    SpannableString spannableString = new SpannableString(fullText);

                    spannableString.setSpan(new AbsoluteSizeSpan(44, true), 0, temperature.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new AbsoluteSizeSpan(38, true), temperature.length(), fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tvTemperatureValue.setText(spannableString);
                }
                if(tvHumidityValue != null) {
                    tvHumidityValue.setText(String.valueOf(data.getHumidity()) + "%");
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
}