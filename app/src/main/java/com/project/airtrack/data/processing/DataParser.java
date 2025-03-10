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
        if(PacketValidator.isValid(data)) {
            // Extract PM2.5 and PM10 values from the data packet according to documentation
            int pm25 = (data[4] << 8) | data[5];
            int pm10 = (data[6] << 8) | data[7];
            // Extract ozone concentration (ppb) from the data packet according to documentation
            float ozone = data[9] + (float) data[10] / 10;
            // Extract temperature and humidity from the data packet according to documentation
            float temperature = data[10] + (float) data[11] / 10;
            float humidity = data[12] + (float) data[13] / 10;
            // Calculate AQI based on the pollutant values
            int pm25AQI = ConcentrationToAQI.pm25(pm25);
            int pm10AQI = ConcentrationToAQI.pm10(pm10);
            int ozoneAQI = ConcentrationToAQI.ozone(ozone);
            Log.i("AQI", "PM2.5 concentration: " + pm25 + " / AQI: " + pm25AQI);
            Log.i("AQI", "PM10 concentration: " + pm10 + " / AQI: " + pm10AQI);
            Log.i("AQI", "Ozone concentration in ppb: " + ozone + " / AQI: " + ozoneAQI);
            Log.i("DHT22", "Temperature: " + temperature + " / Humidity: " + humidity);

            // Temporary
            int timestamp = (int) (System.currentTimeMillis() / 1000);
            ApplicationDatabase db = AirTrackApplication.getDatabase();
            SensorDataDAO sensorDataDAO = db.sensorDataDAO();
            int maximumAQI = selectMaximumAQI(pm25AQI, pm10AQI, ozoneAQI);
            sensorDataDAO.insert(new SensorsData(timestamp, pm25AQI, pm10AQI, ozoneAQI, maximumAQI, temperature, humidity));

            return new EnvironmentalData(timestamp, pm25AQI, pm10AQI, ozoneAQI, maximumAQI, temperature, humidity);
        }
        else {
            throw new DataParsingException("Invalid packet received!");
        }
    }

    private int selectMaximumAQI(int pm25, int pm10, int ozone) {
        return Math.max(Math.max(pm25, pm10), ozone);
    }
}
