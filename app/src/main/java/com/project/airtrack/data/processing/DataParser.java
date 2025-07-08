package com.project.airtrack.data.processing;

import android.util.Log;

import com.project.airtrack.application.AirTrackApplication;
import com.project.airtrack.data.database.ApplicationDatabase;
import com.project.airtrack.data.database.dao.SensorDataDAO;
import com.project.airtrack.data.database.entities.SensorsData;
import com.project.airtrack.exceptions.DataParsingException;
import com.project.airtrack.utils.ConcentrationToAQI;

/**
 * The DataParser class processes raw data packets to extract environmental information.
 */
public class DataParser implements DataProcessor {

    @Override
    public EnvironmentalData process(byte[] data) throws DataParsingException {
        // Extract PM2.5 and PM10 values from the data packet according to documentation
        int pm25 = ((data[4] & 0xFF) << 8) | (data[5] & 0xFF);  // Remove the sign
        int pm10 = ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);  // Remove the sign
        // Extract ozone concentration (ppb) from the data packet according to documentation
        float ozone = ((data[8] & 0xFF) << 8 | (data[9] & 0xFF)) + (float)(data[10] & 0xFF) / 100;    // Remove the sign
        // Extract CO concentration (ppm) from the data packet according to documentation
        float co = ((data[11] & 0xFF) << 8 | (data[12] & 0xFF)) + (float)(data[13] & 0xFF) / 100;    // Remove the sign
        // Extract NO2 concentration (ppm) from the data packet according to documentation
        float no2 = (data[14] & 0xFF) + (float)(data[15] & 0xFF) / 100;  // Remove the sign
        // Extract temperature and humidity from the data packet according to documentation
        float temperature = (data[16] & 0xFF) + (float)(data[17] & 0xFF) / 10;  // Remove the sign
        float humidity = (data[18] & 0xFF) + (float)(data[19] & 0xFF) / 10;  // Remove the sign
        // Calculate AQI based on the pollutant values
        int pm25AQI = ConcentrationToAQI.pm25(pm25);
        int pm10AQI = ConcentrationToAQI.pm10(pm10);
        int ozoneAQI = ConcentrationToAQI.ozone(ozone);
        int coAQI = ConcentrationToAQI.co(co);
        int no2AQI = ConcentrationToAQI.no2(no2);
        Log.i("AQI", "PM2.5 concentration: " + pm25 + " / AQI: " + pm25AQI);
        Log.i("AQI", "PM10 concentration: " + pm10 + " / AQI: " + pm10AQI);
        Log.i("AQI", "Ozone concentration in ppb: " + ozone + " / AQI: " + ozoneAQI);
        Log.i("AQI", "CO concentration in ppm: " + co + " / AQI: " + coAQI);
        Log.i("AQI", "NO2 concentration in ppb: " + no2 + " / AQI: " + no2AQI);
        Log.i("DHT22", "Temperature: " + temperature + " / Humidity: " + humidity);

        int timestamp = (int) (System.currentTimeMillis() / 1000);
        ApplicationDatabase db = AirTrackApplication.getDatabase();
        SensorDataDAO sensorDataDAO = db.sensorDataDAO();
        int maximumAQI = selectMaximumAQI(pm25AQI, pm10AQI, ozoneAQI, coAQI, no2AQI);
        sensorDataDAO.insert(new SensorsData(timestamp, pm25, pm10, ozone, co, no2, maximumAQI, temperature, humidity));

        return new EnvironmentalData(timestamp, pm25, pm10, ozone, co, no2, maximumAQI, temperature, humidity);
    }

    private int selectMaximumAQI(int pm25, int pm10, int ozone, int co, int no2) {
        return Math.max(Math.max(Math.max(pm25, pm10), ozone), Math.max(co, no2));
    }
}
