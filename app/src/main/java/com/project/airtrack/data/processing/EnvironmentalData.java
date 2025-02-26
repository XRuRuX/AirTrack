package com.project.airtrack.data.processing;

/**
 * The EnvironmentalData class stores particulate matter (PM2.5 and PM10) measurements.
 */
public class EnvironmentalData {
    private int timestamp;
    private int pm25AQI;
    private int pm10AQI;
    private int maximumAQI;

    public EnvironmentalData(int timestamp, int pm25AQI, int pm10AQI, int maximumAQI)
    {
        this.timestamp = timestamp;
        this.pm25AQI = pm25AQI;
        this.pm10AQI = pm10AQI;
        this.maximumAQI = maximumAQI;
    }

    public int getTimestamp() { return timestamp; }

    public int getPm25AQI() {
        return pm25AQI;
    }

    public void setPm25AQI(int pm25AQI) {
        this.pm25AQI = pm25AQI;
    }

    public int getPm10AQI() {
        return pm10AQI;
    }

    public void setPm10AQI(int pm10AQI) {
        this.pm10AQI = pm10AQI;
    }

    public int getMaximumAQI() { return maximumAQI; }
}
