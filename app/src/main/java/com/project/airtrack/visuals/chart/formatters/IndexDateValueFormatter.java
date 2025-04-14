package com.project.airtrack.visuals.chart.formatters;

import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The IndexDateValueFormatter class is designed to format and display time-based labels
 * on the X-axis of a chart. This class is used to convert Unix timestamps into readable date labels,
 * adjusting the format depending on the current time.
 */
public class IndexDateValueFormatter extends ValueFormatter {
    private final List<Integer> timestamps;
    private final SimpleDateFormat sameDayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final SimpleDateFormat sameWeekFormat = new SimpleDateFormat("EEE", Locale.getDefault());
    private final SimpleDateFormat sameYearFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
    private final SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

    public IndexDateValueFormatter(List<Integer> timestamps) {
        this.timestamps = timestamps;
    }

    @Override
    public String getFormattedValue(float value) {
        int index = Math.round(value);
        // We only display the label at every fourth point
        if (index % 4 != 0) {
            return "";
        }
        if (index < 0 || index >= timestamps.size()) {
            return "";
        }
        int timestamp = timestamps.get(index);
        Date date = new Date(timestamp * 1000L);

        Calendar calEntry = Calendar.getInstance();
        calEntry.setTime(date);
        Calendar calNow = Calendar.getInstance();

        if (isSameDay(calEntry, calNow)) {
            return sameDayFormat.format(date);
        } else if (isSameWeek(calEntry, calNow)) {
            return sameWeekFormat.format(date);
        } else if (isSameYear(calEntry, calNow)) {
            return sameYearFormat.format(date);
        } else {
            return defaultFormat.format(date);
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isSameWeek(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR);
    }

    private boolean isSameYear(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }
}
