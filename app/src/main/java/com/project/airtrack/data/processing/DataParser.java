package com.project.airtrack.data.processing;

import com.project.airtrack.application.AirTrackApplication;
import com.project.airtrack.data.database.ApplicationDatabase;
import com.project.airtrack.data.database.dao.SensorDataDAO;
import com.project.airtrack.data.database.entities.SensorsData;
import com.project.airtrack.exceptions.DataParsingException;

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

            // Temporary
            ApplicationDatabase db = AirTrackApplication.getDatabase();
            SensorDataDAO sensorDataDAO = db.sensorDataDAO();
            sensorDataDAO.insert(new SensorsData(0, pm25));

            return new EnvironmentalData(pm25, pm10);
        }
        else {
            throw new DataParsingException("Invalid packet received!");
        }
    }
}
