package com.project.airtrack.data.database.entities;

import android.hardware.Sensor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    public int getSensorValue() {
        return sensorValue;
    }
}
