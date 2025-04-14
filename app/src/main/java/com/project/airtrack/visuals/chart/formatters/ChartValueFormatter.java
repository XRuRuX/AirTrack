package com.project.airtrack.visuals.chart.formatters;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Locale;

/**
 * The ChartValueFormatter class is designed to to add the unit for the values on the chart.
 */
public class ChartValueFormatter extends ValueFormatter {
    private final String unit;

    public ChartValueFormatter(String unit) {
        this.unit = unit;
    }

    @Override
    public String getFormattedValue(float value) {
        return String.format(Locale.getDefault(), "%.1f%s", value, unit);
    }
}
