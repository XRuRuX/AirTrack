package com.project.airtrack.data.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Delete;

import com.project.airtrack.data.database.entities.SensorsData;

import java.util.List;

/**
 * Data Access Object (DAO) interface for interacting with the data in the database.
 * This interface provides methods for querying, inserting, and deleting sensor data.
 */
@Dao
public interface SensorDataDAO {
    // Extract all records
    @Query("SELECT * FROM sensors_data")
    List<SensorsData> getAllSensorData();

    // Retrieves the most recent sensor data
    @Query("SELECT * FROM sensors_data ORDER BY id DESC LIMIT 1")
    SensorsData getLastSensorData();

    // Insert single sensor data record intro the database
    @Insert
    void insert(SensorsData data);
}
