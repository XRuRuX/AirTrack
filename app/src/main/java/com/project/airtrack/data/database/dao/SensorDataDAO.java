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
    // Retrieves all sensor data records from the database
    @Query("SELECT * FROM sensors_data")
    List<SensorsData> getAll();

    // Retrieves all sensor data records by specific IDs
    @Query("SELECT * FROM sensors_data WHERE id IN (:sensorsDataIds)")
    List<SensorsData> loadAllByIds(int[] sensorsDataIds);

    // Retrieves the most recent sensor data
    @Query("SELECT * FROM sensors_data ORDER BY id DESC LIMIT 1")
    SensorsData getLastSensorData();

    // Insert single sensor data record intro the database
    @Insert
    void insert(SensorsData data);

    // Inserts multiple sensor data record into the database
    @Insert
    void insertAll(SensorsData... data);

    // Delets a specific sensor data record from the database
    @Delete
    void delete(SensorsData data);
}
