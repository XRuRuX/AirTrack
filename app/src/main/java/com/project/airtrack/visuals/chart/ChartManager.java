package com.project.airtrack.visuals;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.github.mikephil.charting.data.Entry;
import com.project.airtrack.application.AirTrackApplication;
import com.project.airtrack.data.database.ApplicationDatabase;
import com.project.airtrack.data.database.dao.SensorDataDAO;
import com.project.airtrack.data.database.entities.SensorsData;
import com.project.airtrack.R;
import com.project.airtrack.data.processing.EnvironmentalData;

import java.util.ArrayList;
import java.util.List;

/**
 * The ChartManager class is designed to handle multiple charts, manage their configurations, and facilitate the visualization of data by
 * plotting it dynamically. It acts as the central hub for loading and updating chart data, both from database source and
 * real-time live data streams
 */
public class ChartManager {
    private DataChart aqiChart, pm25Chart;
    ArrayList<Integer> timestamps = new ArrayList<>();

    public ChartManager(View view, Context context) {
        aqiChart = new DataChart(view, context, R.id.lineChart_AQI);
        pm25Chart = new DataChart(view, context, R.id.lineChart_pm25);
    }

    public void loadDataFromDatabase(FragmentActivity activity) {
        new Thread(() -> {
            // Access the database
            ApplicationDatabase db = AirTrackApplication.getDatabase();
            SensorDataDAO sensorDataDAO = db.sensorDataDAO();
            List<SensorsData> sensorsDataList = sensorDataDAO.getAllSensorData();

            // Check to see if we have data available
            if (sensorsDataList != null && !sensorsDataList.isEmpty()) {
                ArrayList<Entry> aqiEntries = new ArrayList<>();
                ArrayList<Entry> pm25Entries = new ArrayList<>();

                // Get data from the database
                for (int i = 0; i < sensorsDataList.size(); i++) {
                    SensorsData sensor = sensorsDataList.get(i);
                    aqiEntries.add(new Entry(i, sensor.maximumAQI));
                    pm25Entries.add(new Entry(i, sensor.pm25AQI));
                    timestamps.add(sensor.timestamp);
                }

                // We update the charts on the UI thread
                activity.runOnUiThread(() -> {
                    aqiChart.setChartData(aqiEntries, timestamps);
                    pm25Chart.setChartData(pm25Entries, timestamps);
                });
            }
        }).start();
    }

    public void onLiveDataReceived(EnvironmentalData data) {
        timestamps.add(data.getTimestamp());
        aqiChart.addDataToChart(data.getMaximumAQI(), timestamps);
        pm25Chart.addDataToChart(data.getPm25AQI(), timestamps);
    }
}
