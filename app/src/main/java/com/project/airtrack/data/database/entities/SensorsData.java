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

    @ColumnInfo(name = "sensor_value")
    public int sensorValue;

    public SensorsData(int timestamp, int sensorId, int sensorValue)
    {
        this.timestamp = timestamp;
        this.sensorId = sensorId;
        this.sensorValue = sensorValue;
    }

    public EnvironmentalData toEnvironmentalData()
    {
        return new EnvironmentalData(timestamp, sensorValue, sensorId);
    }
}
