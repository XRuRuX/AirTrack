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

    @ColumnInfo(name = "pm25_AQI")
    public int pm25AQI;

    @ColumnInfo(name = "pm10_AQI")
    public int pm10AQI;

    @ColumnInfo(name = "ozone_AQI")
    public int ozoneAQI;

    @ColumnInfo(name = "max_AQI")
    public int maximumAQI;

    @ColumnInfo(name = "temperature")
    public float temperature;

    @ColumnInfo(name = "humidity")
    public float humidity;

    public SensorsData(int timestamp, int pm25AQI, int pm10AQI, int ozoneAQI, int maximumAQI, float temperature, float humidity)
    {
        this.timestamp = timestamp;
        this.pm25AQI = pm25AQI;
        this.pm10AQI = pm10AQI;
        this.ozoneAQI = ozoneAQI;
        this.maximumAQI = maximumAQI;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public EnvironmentalData toEnvironmentalData()
    {
        return new EnvironmentalData(timestamp, pm25AQI, pm10AQI, ozoneAQI, maximumAQI, temperature, humidity);
    }
}
