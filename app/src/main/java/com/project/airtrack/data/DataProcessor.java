package com.project.airtrack.data;

/**
 * The DataProcessor interface defines a method for processing raw data.
 */
public interface DataProcessor {
    EnvironmentalData process(byte[] data);
}
