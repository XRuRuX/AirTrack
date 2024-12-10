package com.project.airtrack.bluetooth;

import com.project.airtrack.data.DataProcessor;
import com.project.airtrack.data.EnvironmentalData;

/**
 * The DataMediator class acts as a bridge between the data processor and the listener for the UI.
 */
public class DataMediator implements Mediator {
    private DataProcessor processor;
    private OnDataReceivedListener dataReceivedListener;

    public DataMediator(DataProcessor processor, OnDataReceivedListener listener)
    {
        this.processor = processor;
        this.dataReceivedListener = listener;
    }

    // Processes raw data and notifies the listener if valid data is produced
    @Override
    public void handleData(byte[] data) {
        EnvironmentalData processedData = processor.process(data);

        if(processedData != null) {
            dataReceivedListener.onDataReceived(String.valueOf(processedData.getPm25()));
        }
    }
}
