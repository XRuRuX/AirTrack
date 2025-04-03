class PMS5003():
    def validate_packet(raw_data):
         # Check packet size
        if len(raw_data) != 32:
             print("Invalid packet size\n")
             return False
        
        # Check packet start signature (0x42, 0x4d)
        if raw_data[0] != 0x42 or raw_data[1] != 0x4d:
             print("Invalid packet signature\n")
             return False

         # Check checksum
        checksum = sum(raw_data[:30]) & 0xFFFF # It only holds the last 16 bits of the sum because the recived checksum will be on only 16 bits
        received_checksum = (raw_data[30] << 8) | raw_data[31]
        if checksum != received_checksum:
            print("Invalid checksum!\n")
            return False
        
        return True
             
    def get_data(raw_data):
        if PMS5003.validate_packet(raw_data):
            pm2_5 = (raw_data[12] << 8) | raw_data[13]
            pm10 = (raw_data[14] << 8) | raw_data[15]

            return {"PM2.5": pm2_5, "PM10": pm10}
        else:
            return None