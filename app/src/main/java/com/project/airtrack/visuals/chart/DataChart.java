package com.project.airtrack.visuals.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.project.airtrack.visuals.chart.formatters.ChartValueFormatter;
import com.project.airtrack.visuals.chart.formatters.IndexDateValueFormatter;
import com.project.airtrack.R;

import java.util.ArrayList;
import java.util.List;

/**
 * The DataChart class is responsible for configuring and styling individual charts, as well as plotting the data. It can be used for both live
 * and database data sources, allowing dynamic and interactive visualizations. This class encapsulates the logic for viewing charts with
 * different styling options and facilitates the real-time plotting of data
 */
public class DataChart {
    private static final int MAX_VISIBLE_ENTRIES = 7;
    private LineChart lineChart;
    private LineDataSet dataSet;
    private float lastXValue = 0;
    private String unit;

    public DataChart(@NonNull View view, Context context, int chartID, String unit) {
        lineChart = view.findViewById(chartID);

        // Create the list of points (X, Y)
        ArrayList<Entry> entries = new ArrayList<>();
        dataSet = new LineDataSet(entries, "Values");

        // Unit of values on the chart
        this.unit = unit;

        // Customize the style
        styleChart(context);
    }

    private void styleChart(Context context) {
        // Line
        int leafyGreen = ContextCompat.getColor(context, R.color.leafy_green);
        dataSet.setColor(leafyGreen);                       // Line color
        dataSet.setLineWidth(2f);                           // Line thickness
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);     // Makes the line smoother

        // Dots
        dataSet.setCircleRadius(4f); // Dots size
        dataSet.setCircleHoleRadius(3f); // Interior dots size
        int greenForest = ContextCompat.getColor(context, R.color.green_forest);
        dataSet.setCircleColor(greenForest); // Dots exterior color
        dataSet.setCircleHoleColor(Color.WHITE); // Dots interior color

        dataSet.setValueTextSize(12f); // Text size

        // Allows filling the chart below the line
        dataSet.setDrawFilled(true);

        // Create a gradient for the fill
        int tropicalGreen = ContextCompat.getColor(context, R.color.tropical_green);
        int veryDarkGreen = ContextCompat.getColor(context, R.color.very_dark_green);
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{tropicalGreen, veryDarkGreen} // From green to dark green
        );
        gradientDrawable.setCornerRadius(0f);
        dataSet.setFillDrawable(gradientDrawable); // Apply gradient
        dataSet.setFillAlpha(255);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Disable the chart description
        lineChart.getDescription().setEnabled(false);

        // Remove the background grid
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        // Change axis color
        int white = ContextCompat.getColor(context, R.color.secondary_white);
        lineChart.getAxisLeft().setAxisLineColor(white);
        lineChart.getXAxis().setAxisLineColor(white);

        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP_INSIDE);   // Moves the X-axis labels to the top of the chart and inside the chart
        lineChart.getXAxis().setAvoidFirstLastClipping(true);               // First and last labels are not clipped or cut off at the edges of the chart

        // Labels
        lineChart.getXAxis().setTextColor(Color.WHITE);     // Sets the color of the X-axis labels to white
        lineChart.getAxisLeft().setTextColor(Color.WHITE);  // Set the color white for the left Y-axis labels
        lineChart.getAxisRight().setTextColor(Color.WHITE); // Set the color white for the right Y-axis labels

        // Disable the right axis (for cleaner look)
        lineChart.getAxisRight().setEnabled(false);

        // Removes extra space above and below on the left axis
        lineChart.getAxisLeft().setSpaceTop(30f);
        lineChart.getAxisLeft().setSpaceBottom(2f);

        // Enable gestures
        lineChart.setDragEnabled(true);                 // Allows dragging the chart
        lineChart.setScaleEnabled(true);                // Allows zoom
        lineChart.setPinchZoom(true);                   // Zoom with two fingers
        lineChart.setDoubleTapToZoomEnabled(false);     // Disable zoom by double clicking

        // Disable highlight lines
        dataSet.setHighlightEnabled(false);
        lineChart.getData().setHighlightEnabled(false);

        // Disable the legend
        lineChart.getLegend().setEnabled(false);

        // Disable the left axis
        lineChart.getAxisLeft().setEnabled(false);

        // Disable X axis
        lineChart.getXAxis().setDrawAxisLine(false);

        // Values above the dots
        dataSet.setDrawValues(true);
        int primaryWhite = ContextCompat.getColor(context, R.color.primary_white);
        dataSet.setValueTextColor(primaryWhite);
        dataSet.setValueTextSize(10f);

        // Offsets for better positioning
        lineChart.getXAxis().setYOffset(-2f);
        lineChart.setViewPortOffsets(-50f, 30f, -50f, 0f);

        // Crop the first and last values to better see the chart
        lineChart.getXAxis().setAvoidFirstLastClipping(true);

        lineChart.invalidate(); // Redraw
    }

    public void setChartData(List<Entry> entries, List<Integer> timestamps) {
        // Clean up the existing dataset
        dataSet.clear();
        // Add the new entries
        for (Entry entry : entries) {
            dataSet.addEntry(entry);
        }

        // We notify the chart that the data has changed
        lineChart.getData().notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.setVisibleXRangeMaximum(MAX_VISIBLE_ENTRIES);
        if (dataSet.getEntryCount() > 0) {
            float lastX = dataSet.getEntryForIndex(dataSet.getEntryCount() - 1).getX();
            lineChart.moveViewToX(lastX);
            lastXValue = lastX;
        }
        // We set the formatter for the X axis using timestamps
        lineChart.getXAxis().setValueFormatter(new IndexDateValueFormatter(timestamps));
        // TODO
        dataSet.setValueFormatter(new ChartValueFormatter(unit));
        lineChart.invalidate();
    }

    public void addDataToChart(float value, ArrayList<Integer> timestamps) {
        // We continue from the last value on the X axis
        float newX = lastXValue + 1;
        lastXValue = newX;

        addNewEntry(newX, value);

        // We update the X-axis formatter with the new timestamps
        lineChart.getXAxis().setValueFormatter(new IndexDateValueFormatter(timestamps));

        // TODO
        dataSet.setValueFormatter(new ChartValueFormatter(unit));

        // Move the view to show the last point added
        lineChart.moveViewToX(dataSet.getEntryCount());
    }

    private void addNewEntry(float x, float y) {
        // Check if the line chart has data and at least one dataset
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            dataSet.addEntry(new Entry(x, y));

            // Notify the chart that the data has changed
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();

            // Set the maximum number of visible entries in the chart
            lineChart.setVisibleXRangeMaximum(MAX_VISIBLE_ENTRIES);

            // Move the chart view to the latest entry
            lineChart.moveViewToX(dataSet.getEntryCount());

            // Redraw the chart to reflect the changes
            lineChart.invalidate();
        }
    }
}
