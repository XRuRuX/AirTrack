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

import com.github.mikephil.charting.data.Entry;
import com.project.airtrack.application.AirTrackApplication;
import com.project.airtrack.bluetooth.OnDataReceivedListener;
import com.project.airtrack.data.database.ApplicationDatabase;
import com.project.airtrack.data.database.dao.SensorDataDAO;
import com.project.airtrack.data.database.entities.SensorsData;
import com.project.airtrack.data.processing.EnvironmentalData;
import com.project.airtrack.visuals.chart.DataChart;

import android.text.SpannableString;

import java.util.ArrayList;
import java.util.List;

/**
 * TemperatureFragment is responsible for displaying detailed information about
 * temperature and humidity
 * Additional functionality for showing temperature and humidity data will be added in the future.
 */
public class TemperatureFragment extends Fragment implements OnDataReceivedListener {
    private TextView tvTemperatureValue;
    private TextView tvHumidityValue;
    private DataChart temperatureChart, humidityChart;
    ArrayList<Integer> timestamps = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = initializeLayout(inflater, container);
        updateUIWithLastSensorData();

        return view;
    }

    // Initializes the layout for the fragment and retrieves references to UI components
    private View initializeLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);
        tvTemperatureValue = view.findViewById(R.id.tv_temperature_value);
        tvHumidityValue = view.findViewById(R.id.tv_humidity_value);
        temperatureChart = new DataChart(view, requireContext(), R.id.lineChart_temperature, "°C");
        humidityChart = new DataChart(view, requireContext(), R.id.lineChart_humidity, "%");
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

                List<SensorsData> sensorsDataList = sensorDataDAO.getAllSensorData();
                // Check to see if we have data available
                if (sensorsDataList != null && !sensorsDataList.isEmpty()) {
                    ArrayList<Entry> temperatureEntries = new ArrayList<>();
                    ArrayList<Entry> humidityEntries = new ArrayList<>();

                    // Get data from the database
                    for (int i = 0; i < sensorsDataList.size(); i++) {
                        SensorsData sensor = sensorsDataList.get(i);
                        temperatureEntries.add(new Entry(i, sensor.temperature));
                        humidityEntries.add(new Entry(i, sensor.humidity));
                        timestamps.add(sensor.timestamp);
                    }

                    // We update the charts on the UI thread
                    requireActivity().runOnUiThread(() -> {
                        temperatureChart.setChartData(temperatureEntries, timestamps);
                        humidityChart.setChartData(humidityEntries, timestamps);
                    });
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

                    // Spannable strings so that the size between value and unit is different
                    SpannableString spannableString = new SpannableString(fullText);
                    spannableString.setSpan(new AbsoluteSizeSpan(50, true), 0, temperature.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new AbsoluteSizeSpan(42, true), temperature.length(), fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tvTemperatureValue.setText(spannableString);
                }
                if(tvHumidityValue != null) {
                    String humidity = String.valueOf(data.getHumidity());
                    String fullText = humidity + "%";

                    // Spannable strings so that the size between value and unit is different
                    SpannableString spannableString = new SpannableString(fullText);
                    spannableString.setSpan(new AbsoluteSizeSpan(50, true), 0, humidity.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new AbsoluteSizeSpan(42, true), humidity.length(), fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tvHumidityValue.setText(spannableString);
                }
            });
        }
        timestamps.add(data.getTimestamp());
        temperatureChart.addDataToChart(data.getTemperature(), timestamps);
        humidityChart.addDataToChart(data.getHumidity(), timestamps);
    }
}