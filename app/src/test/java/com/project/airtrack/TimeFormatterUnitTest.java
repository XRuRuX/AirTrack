package com.project.airtrack;

import com.project.airtrack.utils.TimeFormatter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The TimeFormatterUnitTest class tests the functionality of the TimeFormatter utility class.
 * It verifies that the secondsToStringFormat method returns the correct readable time strings
 * for a variety of input cases, including edge cases like negative values and large time intervals.
 */
public class TimeFormatterUnitTest {
    @ParameterizedTest
    @CsvSource({
            "-23, Invalid",
            "0, Less than one minute ago",
            "30, Less than one minute ago",
            "60, 1 minute ago",
            "150, 2 minutes ago",
            "3600, 1 hour ago",
            "25500, 7 hours ago",
            "86400, 1 day ago",
            "375600, 4 days ago",
            "604800, 1 week ago",
            "1964400, 3 weeks ago",
            "2419200, 1 month ago",
            "14148719, 5 months ago",
            "31557600, 1 year ago",
            "83113851, 2 years ago"
    })
    public void testTimeFormatter(int seconds, String expected) {
        assertEquals(expected, TimeFormatter.secondsToStringFormat(seconds));
    }
}
