package com.project.airtrack.data.processing;

/**
 * The EnvironmentalData class stores particulate matter (PM2.5 and PM10) and temperature and humidity measurements.
 */
public class EnvironmentalData {
    private int timestamp;
    private int pm25;
    private int pm10;
    private float ozone;
    private float co;
    private float no2;
    private int maximumAQI;
    private float temperature;
    private float humidity;

    public EnvironmentalData(int timestamp, int pm25, int pm10, float ozone, float co, float no2, int maximumAQI, float temperature, float humidity)
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

    public int getTimestamp() { return timestamp; }

    public int getPm25() {
        return pm25;
    }

    public int getPm10() {
        return pm10;
    }

    public float getOzone() {
        return ozone;
    }

    public float getCo() {
        return co;
    }

    public float getNo2() {
        return no2;
    }

    public int getMaximumAQI() { return maximumAQI; }

    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
}
