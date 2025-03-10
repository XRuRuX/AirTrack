package com.project.airtrack.data.processing;

/**
 * The EnvironmentalData class stores particulate matter (PM2.5 and PM10) and temperature and humidity measurements.
 */
public class EnvironmentalData {
    private int timestamp;
    private int pm25AQI;
    private int pm10AQI;
    private int ozoneAQI;
    private int maximumAQI;
    private float temperature;
    private float humidity;

    public EnvironmentalData(int timestamp, int pm25AQI, int pm10AQI, int ozoneAQI, int maximumAQI, float temperature, float humidity)
    {
        this.timestamp = timestamp;
        this.pm25AQI = pm25AQI;
        this.pm10AQI = pm10AQI;
        this.ozoneAQI = ozoneAQI;
        this.maximumAQI = maximumAQI;
        this.temperature = temperature;
        this.humidity = humidity;
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

    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
}
