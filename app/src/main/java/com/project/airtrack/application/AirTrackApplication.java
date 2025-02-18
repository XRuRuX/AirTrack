package com.project.airtrack.application;

import android.app.Application;

import androidx.room.Room;

import com.project.airtrack.data.database.ApplicationDatabase;

/**
 * AirTrackApplication class serves as the entry point for the application.
 * It can be used to initialize and manage global resources.
 */
public class AirTrackApplication extends Application {
    private static ApplicationDatabase database;    // Static reference to a global resource (database) shared across the application

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(), ApplicationDatabase.class, "sensors_database").build();
    }

    public static ApplicationDatabase getDatabase() {
        return database;
    }

}
