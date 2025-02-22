package com.project.airtrack.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.project.airtrack.data.database.dao.SensorDataDAO;
import com.project.airtrack.data.database.entities.SensorsData;

/**
 * The ApplicationDatabase class is the main database class for the application.
 * It provides access to the necessary DAOs (Data Access Objects) and manages database entities.
 */
@Database(entities = {SensorsData.class}, version = 2)
public abstract class ApplicationDatabase extends RoomDatabase {
    public abstract SensorDataDAO sensorDataDAO();
}
