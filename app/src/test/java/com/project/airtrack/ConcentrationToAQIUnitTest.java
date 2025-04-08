package com.project.airtrack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.project.airtrack.utils.ConcentrationToAQI;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ConcentrationToAQIUnitTest {
    @ParameterizedTest
    @CsvSource({
            "-15, 0",
            "0, 0",
            "10, 53",
            "28, 86",
            "38, 107",
            "133, 208",
            "179, 254",
            "344, 538",
            "444, 738",
            "800, 999"

    })
    public void testConcentrationToAQIpm25(int pm25, int expected) {
        assertEquals(expected, ConcentrationToAQI.pm25(pm25));
    }

    @ParameterizedTest
    @CsvSource({
            "-15, 0",
            "0, 0",
            "51, 47",
            "130, 88",
            "222, 134",
            "340, 193",
            "360, 209",
            "444, 323",
            "863, 762",
            "1206, 999"

    })
    public void testConcentrationToAQIpm10(int pm10, int expected) {
        assertEquals(expected, ConcentrationToAQI.pm10(pm10));
    }

    @ParameterizedTest
    @CsvSource({
            "-22, 0",
            "0, 0",
            "23, 21",
            "66, 88",
            "84, 147",
            "87, 156",
            "156, 254",
            "323, 260",
            "700, 447",
            "1234, 999"
    })
    public void testConcentrationToAQIozone(int ozone, int expected) {
        assertEquals(expected, ConcentrationToAQI.ozone(ozone));
    }

    @ParameterizedTest
    @CsvSource({
            "-29, 0",
            "0, 0",
            "2.3, 26",
            "6.6, 72",
            "11.2, 130",
            "14.7, 188",
            "22.8, 250",
            "38, 305",
            "700, 699",
            "1234, 999"
    })
    public void testConcentrationToAQIco(float co, int expected) {
        assertEquals(expected, ConcentrationToAQI.co(co));
    }

    @ParameterizedTest
    @CsvSource({
            "-29, 0",
            "0, 0",
            "0.042, 40",
            "0.057, 54",
            "0.233, 126",
            "0.561, 185",
            "1.222, 296",
            "1.9, 348",
            "7, 700",
            "13, 999"
    })
    public void testConcentrationToAQIno2(float no2, int expected) {
        assertEquals(expected, ConcentrationToAQI.no2(no2));
    }
}
