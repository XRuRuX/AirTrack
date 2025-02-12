package com.project.airtrack.exceptions;

/**
 * The DataParsingException class is a custom exception class for handling errors
 * that occur during the data parsing process when the data is found to be invalid or corrupted.
 */
public class DataParsingException extends Exception {
    public DataParsingException(String message) {
        super(message);
    }
}
