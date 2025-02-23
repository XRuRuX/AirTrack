package com.project.airtrack.utils;

/**
 * The TimeFormatter class provides utility methods for formatting time-related values.
 * It converts a given number of seconds into a readable time format.
 */
public class TimeFormatter {
    public static String secondsToStringFormat(int seconds) {
        int temp;
        String unit;

        if(seconds < 0)
        {
            return "Invalid";
        }
        else if(seconds < 60)
        {
            return "Less than one minute ago";
        }

        // Determine the unit and calculate values for minutes, hours, days, etc
        else if(seconds < 3600)
        {
            temp = seconds / 60;
            unit = "minute";
        }
        else if(seconds < 86400)
        {
            temp = seconds / 3600;
            unit = "hour";
        }
        else if(seconds < 604800)
        {
            temp = seconds / 86400;
            unit = "day";
        }
        else if(seconds < 2419200)
        {
            temp = seconds / 604800;
            unit = "week";
        }
        else if(seconds < 31557600)
        {
            temp = seconds / 2419200;
            unit = "month";
        }
        else
        {
            temp = seconds / 31557600;
            unit = "year";
        }

        if(temp == 1) {
            return temp + " " + unit + " ago";
        }
        else {
            return temp + " " + unit + "s ago";
        }
    }
}
