def crc16_ccitt(data, poly=0x1021, init_crc=0x0000):
    crc = init_crc
    for byte in data:
        crc ^= byte << 8
        for _ in range(8):
            if crc & 0x8000:
                crc = (crc << 1) ^ poly
            else:
                crc <<= 1
            crc &= 0xFFFF  
    return crc

class PacketBuilder:
    @staticmethod
    def encode(start, id, length, pm25, pm10, ozone, co, no2, temperature, humidity):
        temp = bytearray()
        temp += start.encode('utf-8') # Add start signature as UTF-8 bytes
        temp.append(id & 0xFF)  # Add packet ID
        temp.append(length & 0xFF) # Add packet length
        temp += pm25.to_bytes(2, 'big') # Add PM2.5 value (2 bytes, big-endian)
        temp += pm10.to_bytes(2, 'big') # Add PM10 value (2 bytes, big-endian)
        ozone_int = int(ozone) # Only the integer part
        ozone_decimal = int((ozone - ozone_int) * 10) # Decimal part
        temp += ozone_int.to_bytes(2, 'big')
        temp += ozone_decimal.to_bytes(1, 'big')
        co_int = int(co) # Only the integer part
        co_decimal = int((co - co_int) * 100) # Decimal part
        temp += co_int.to_bytes(2, 'big')
        temp += co_decimal.to_bytes(1, 'big')
        no2_int = int(no2) # Only the integer part
        no2_decimal = int((no2 - no2_int) * 100) # Decimal part
        temp += no2_int.to_bytes(1, 'big')
        temp += no2_decimal.to_bytes(1, 'big')
        temperature_int = int(temperature) # Only the integer part
        temperature_decimal = int((temperature - temperature_int) * 10) # Decimal part
        temp += temperature_int.to_bytes(1, 'big')
        temp += temperature_decimal.to_bytes(1, 'big')
        humidity_int = int(humidity) # Only the integer part
        humidity_decimal = int((humidity - humidity_int) * 10) # Decimal part
        temp += humidity_int.to_bytes(1, 'big')
        temp += humidity_decimal.to_bytes(1, 'big')
        crc = crc16_ccitt(temp)
        temp += crc.to_bytes(2, 'big')  # Append checksum (2 bytes, big-endian)
        return temp
