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
    private TextView tvLiveAQITop;
    private TextView tvLiveAQI;
    private TextView tvLivePm25;
    private TextView tvLivePm10;
    private TextView tvLiveOzone;
    private TextView tvLiveCo;
    private TextView tvLiveNo2;
    ChartManager chartManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = initializeLayout(inflater, container);
        updateUIWithLastSensorData();

        chartManager = new ChartManager(view, requireContext());
        chartManager.loadDataFromDatabase(requireActivity());

        return view;
    }

    // Initializes the layout for the fragment and retrieves references to UI components
    private View initializeLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_air, container, false);
        tvLiveAQITop = view.findViewById(R.id.tv_aqi_value);
        tvLiveAQI = view.findViewById(R.id.tv_live_aqi);
        tvLivePm25 = view.findViewById(R.id.tv_live_pm25);
        tvLivePm10 = view.findViewById(R.id.tv_live_pm10);
        tvLiveOzone = view.findViewById(R.id.tv_live_ozone);
        tvLiveCo = view.findViewById(R.id.tv_live_co);
        tvLiveNo2 = view.findViewById(R.id.tv_live_no2);

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
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if(tvLiveAQITop != null) {
                    tvLiveAQITop.setText(String.valueOf(data.getMaximumAQI()));
                }
                if (tvLiveAQI != null) {
                    tvLiveAQI.setText(String.valueOf(data.getMaximumAQI()));
                }
                if(tvLivePm25 != null) {
                    tvLivePm25.setText(data.getPm25() + " µg/m³");
                }
                if(tvLivePm10 != null) {
                    tvLivePm10.setText(data.getPm10() + " µg/m³");
                }
                if(tvLiveOzone != null) {
                    tvLiveOzone.setText(data.getOzone() + " ppb");
                }
                if(tvLiveCo != null) {
                    tvLiveCo.setText(data.getCo() + " ppm");
                }
                if(tvLiveNo2 != null) {
                    tvLiveNo2.setText(data.getNo2() + " ppb");
                }
                // Stream live data to ChartManager for chart updates
                chartManager.onLiveDataReceived(data);
            });
        }
    }
}