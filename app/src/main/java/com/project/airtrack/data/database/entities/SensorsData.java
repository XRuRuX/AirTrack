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

    @ColumnInfo(name = "pm25")
    public int pm25;

    @ColumnInfo(name = "pm10")
    public int pm10;

    @ColumnInfo(name = "ozone")
    public float ozone;

    @ColumnInfo(name = "co")
    public float co;

    @ColumnInfo(name = "no2")
    public float no2;

    @ColumnInfo(name = "max_AQI")
    public int maximumAQI;

    @ColumnInfo(name = "temperature")
    public float temperature;

    @ColumnInfo(name = "humidity")
    public float humidity;

    public SensorsData(int timestamp, int pm25, int pm10, float ozone, float co, float no2, int maximumAQI, float temperature, float humidity)
    {
        this.timestamp = timestamp;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.ozone = ozone;
        this.co = co;
        this.no2 = no2;
        this.maximumAQI = maximumAQI;
        this.temperature = temperature;
        this.humidity = humidity;
    }



    public EnvironmentalData toEnvironmentalData()
    {
        return new EnvironmentalData(timestamp, pm25, pm10, ozone, co, no2, maximumAQI, temperature, humidity);
    }
}
