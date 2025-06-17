package com.project.airtrack.data.processing;

import android.util.Log;

/**
 * The PacketValidator class validates the structure, length, and integrity of a data packet.
 */
public class PacketValidator {
    public static boolean isValid(byte[] data) {
        // Temporary for errors future implementation on error management system
        if(data[0] == (byte)0xFF)
        {
            return false;
        }

        // Check packet start signature
        int packetStart = (data[0] << 8) | data[1];
        if(packetStart != 16695) {  // 41 = 'A' in ASCII and 37 = '7' in ASCII. 0x4137 = 16695 (decimal)
            Log.e("PacketValidator", "Wrong packet signature. Expected: 16695. Actual" + packetStart);
            return false;
        }

        // Check if the packet length matches the expected length
        int expectedPacketLength = data[3];
        int actualPacketLength = data.length;
        if(expectedPacketLength != actualPacketLength) {
            Log.e("PacketValidator", "Wrong packet length. Expected: " + expectedPacketLength + ". Actual: " + actualPacketLength);
            return false;
        }

        // Check the CRC-16 checksum
        int expectedCRC16 = ((data[20] & 0xFF) << 8) | (data[21] & 0xFF);
        int actualCRC16 = CRC16.calculateCRC(data, actualPacketLength - 2);
        if(expectedCRC16 != actualCRC16) {
            Log.e("PacketValidator", "Wrong packet CRC-16. Expected: " + expectedCRC16 + ". Actual: " + actualCRC16);
            return false;
        }
        return true;
    }
}
