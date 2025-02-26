package com.project.airtrack.data.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.project.airtrack.data.processing.EnvironmentalData;

@Entity(tableName = "sensors_data")
public class SensorsData {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "timestamp")
    public int timestamp;

    @ColumnInfo(name = "sensor_id")
    public int sensorId;

    @ColumnInfo(name = "pm25_AQI")
    public int pm25AQI;

    @ColumnInfo(name = "pm10_AQI")
    public int pm10AQI;

    @ColumnInfo(name = "max_AQI")
    public int maximumAQI;

    public SensorsData(int timestamp, int sensorId, int pm25AQI, int pm10AQI, int maximumAQI)
    {
        this.timestamp = timestamp;
        this.sensorId = sensorId;
        this.pm25AQI = pm25AQI;
        this.pm10AQI = pm10AQI;
        this.maximumAQI = maximumAQI;
    }

    public EnvironmentalData toEnvironmentalData()
    {
        return new EnvironmentalData(timestamp, pm25AQI, pm10AQI, maximumAQI);
    }
}
