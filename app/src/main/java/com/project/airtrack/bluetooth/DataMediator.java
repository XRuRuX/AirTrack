package com.project.airtrack.bluetooth;

import com.project.airtrack.data.processing.DataProcessor;
import com.project.airtrack.data.processing.EnvironmentalData;
import com.project.airtrack.exceptions.DataParsingException;

/**
 * The DataMediator class acts as a bridge between the data processor and the listener for the UI.
 */
public class DataMediator implements Mediator {
    private DataProcessor processor;
    private OnDataReceivedListener homeDataReceivedListener;
    private OnDataReceivedListener temperatureDataReceivedListener;
    private OnDataReceivedListener airDataReceivedListener;

    public DataMediator(DataProcessor processor, OnDataReceivedListener homeListener, OnDataReceivedListener temperatureListener, OnDataReceivedListener airDataReceivedListener)
    {
        this.processor = processor;
        this.homeDataReceivedListener = homeListener;
        this.temperatureDataReceivedListener = temperatureListener;
        this.airDataReceivedListener = airDataReceivedListener;
    }

    // Processes raw data and notifies the listener if valid data is produced
    @Override
    public void handleData(byte[] data) {
        try {
            EnvironmentalData processedData = processor.process(data);
            // Send data to all fragment listeners
            homeDataReceivedListener.onDataReceived(processedData);
            temperatureDataReceivedListener.onDataReceived(processedData);
            airDataReceivedListener.onDataReceived(processedData);

        } catch (DataParsingException e)
        {
            // Future implementation on error management system
        }

    }
}
