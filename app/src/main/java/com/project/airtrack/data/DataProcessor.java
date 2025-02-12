package com.project.airtrack.data;

import com.project.airtrack.exceptions.DataParsingException;

/**
 * The DataProcessor interface defines a method for processing raw data.
 */
public interface DataProcessor {
    EnvironmentalData process(byte[] data) throws DataParsingException;
}
