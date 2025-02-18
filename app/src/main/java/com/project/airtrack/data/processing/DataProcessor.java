package com.project.airtrack.data.processing;

import com.project.airtrack.exceptions.DataParsingException;

/**
 * The DataProcessor interface defines a method for processing raw data.
 */
public interface DataProcessor {
    EnvironmentalData process(byte[] data) throws DataParsingException;
}
