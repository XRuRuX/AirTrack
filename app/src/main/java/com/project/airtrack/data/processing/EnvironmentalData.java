package com.project.airtrack.data.processing;

/**
 * The EnvironmentalData class stores particulate matter (PM2.5 and PM10) measurements.
 */
public class EnvironmentalData {
    private int timestamp;
    private int pm25;
    private int pm10;

    public EnvironmentalData(int timestamp, int pm25, int pm10)
    {
        this.timestamp = timestamp;
        this.pm25 = pm25;
        this.pm10 = pm10;
    }

    public int getTimestamp() { return timestamp; }

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public int getPm10() {
        return pm10;
    }

    public void setPm10(int pm10) {
        this.pm10 = pm10;
    }
}
