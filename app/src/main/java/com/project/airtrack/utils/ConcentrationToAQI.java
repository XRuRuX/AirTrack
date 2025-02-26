package com.project.airtrack.utils;

public class ConcentrationToAQI {
    public static int pm25(int pm25) {
        if(pm25 < 0)
        {
            return 0;
        } else if (pm25 <= 9.0) {
            return calculateAQI(pm25, 0.0, 9.0, 0, 50);
        } else if (pm25 >= 9.1 && pm25 <= 35.4) {
            return calculateAQI(pm25, 9.1, 35.4, 51, 100);
        } else if (pm25 >= 35.5 && pm25 <= 55.4) {
            return calculateAQI(pm25, 35.5, 55.4, 101, 150);
        } else if (pm25 >= 55.5 && pm25 <= 125.4) {
            return calculateAQI(pm25, 55.5, 125.4, 151, 200);
        } else if (pm25 >= 125.5 && pm25 <= 225.4) {
            return calculateAQI(pm25, 125.5, 225.4, 201, 300);
        } else if (pm25 >= 225.5 && pm25 <= 325.4) {
            return calculateAQI(pm25, 225.5, 325.4, 301, 500);
        } else if(pm25 >= 325.5 && pm25 <= 500) {
            return calculateAQI(pm25, 325.5, 500, 501, 850);
        }
        else {
            return 999; // maximum value to be represented
        }
    }

    public static int pm10(int pm10) {
        if (pm10 < 0) {
            return 0;
        } else if (pm10 <= 54.0) {
            return calculateAQI(pm10, 0.0, 54.0, 0, 50);
        } else if (pm10 >= 54.1 && pm10 <= 154) {
            return calculateAQI(pm10, 54.1, 154, 51, 100);
        } else if (pm10 >= 154.1 && pm10 <= 254) {
            return calculateAQI(pm10, 154.1, 254, 101, 150);
        } else if (pm10 >= 254.1 && pm10 <= 354) {
            return calculateAQI(pm10, 254.1, 354, 151, 200);
        } else if (pm10 >= 354.1 && pm10 <= 424) {
            return calculateAQI(pm10, 354.1, 424, 201, 300);
        } else if (pm10 >= 424.1 && pm10 <= 604) {
            return calculateAQI(pm10, 424.1, 604, 301, 500);
        } else if (pm10 >= 604.1 && pm10 <= 1000) {
            return calculateAQI(pm10, 604.1, 1000, 501, 900);
        }
        else {
            return 999; // maximum value to be represented
        }
    }

    private static int calculateAQI(double concentration, double cLow, double cHigh, int aqiLow, int aqiHigh) {
        double aqi = ((aqiHigh - aqiLow) / (cHigh - cLow)) * (concentration - cLow) + aqiLow;
        return Math.round((float) aqi);
    }
}
