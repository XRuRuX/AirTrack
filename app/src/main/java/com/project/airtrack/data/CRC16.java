package com.project.airtrack.data;

/**
 * The CRC16 class provides a method to calculate the CRC-16 checksum for a given data array.
 */
public class CRC16 {
    public static int calculateCRC(byte[] data, int byteCount) {
        int crc = 0x0000;
        int polynomial = 0x1021; // CRC-16 polynomial

        for (int i = 0; i < byteCount; i++) {
            crc ^= (data[i] & 0xFF) << 8;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ polynomial;
                } else {
                    crc = crc << 1;
                }
            }
            crc &= 0xFFFF;  // Ensure the CRC remains within 16 bits
        }

        return crc;
    }
}
