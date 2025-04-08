package com.project.airtrack.utils;

import com.project.airtrack.data.processing.EnvironmentalData;

/**
 * The AQIFormatter class provides utility methods for formatting AQI values to different formats.
 */
public class AQIFormatter {
    public static String toString(int aqi) {
        if(aqi < 0) {
            return "GOOD";
        } else if (aqi <= 50) {
            return "GOOD";
        } else if (aqi <= 100) {
            return "MODERATE";
        } else if (aqi <= 150) {
            return "UNHEALTHY";
        } else if (aqi <= 200) {
            return "UNHEALTHY";
        } else if (aqi <= 300) {
            return "VERY UNHEALTHY";
        } else {
            return "HAZARDOUS";
        }
    }

    // Converts the AQI to a specific category and updates the number of segments colored based on the category
    public static int toProgress(int aqi) {
        int temp = 0;

        if(aqi < 0) {
            temp = 0;
        } else if (aqi <= 50) {
            temp = 1;
        } else if (aqi <= 100) {
            temp = 2;
        } else if (aqi <= 150) {
            temp = 3;
        } else if (aqi <= 200) {
            temp = 4;
        } else if (aqi <= 300) {
            temp = 5;
        } else {
            temp = 6;
        }

        return temp;
    }

    // Return a String with the name of the pollutant with the highest AQI
    public static String getPollutantWithHighestAQI(EnvironmentalData data) {
        int pm25 = data.getPm25AQI();
        int pm10 = data.getPm10AQI();
        int ozone = data.getOzoneAQI();
        int co = data.getCoAQI();
        int no2 = data.getNo2AQI();
        int maximumAQI = data.getMaximumAQI();

        if (maximumAQI == pm25) {
            return "PM2.5";
        } else if (maximumAQI == pm10) {
            return "PM10";
        } else if (maximumAQI == ozone) {
            return "OZONE";
        } else if (maximumAQI == co) {
            return "CARBON MONOXIDE";
        } else if (maximumAQI == no2) {
            return "NITROGEN DIOXIDE";
        }
        return "AIR QUALITY";
    }
}
